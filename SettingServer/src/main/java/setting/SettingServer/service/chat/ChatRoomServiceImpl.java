package setting.SettingServer.service.chat;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import setting.SettingServer.common.chat.ChatRoomAccessDeniedException;
import setting.SettingServer.common.chat.ChatRoomNotFoundException;
import setting.SettingServer.common.exception.UserNotFoundException;
import setting.SettingServer.config.SecurityUtil;
import setting.SettingServer.dto.chat.ChatDto;
import setting.SettingServer.dto.chat.ChatMessageInfo;
import setting.SettingServer.dto.chat.ChatUserInfoDto;
import setting.SettingServer.entity.Member;
import setting.SettingServer.entity.chat.ChatMessage;
import setting.SettingServer.entity.chat.ChatRoom;
import setting.SettingServer.repository.chat.ChatMessageRepository;
import setting.SettingServer.repository.chat.ChatRoomRepository;
import setting.SettingServer.repository.MemberRepository;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatRoomServiceImpl implements ChatRoomService {

    private final MemberRepository memberRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final SecurityUtil securityUtil;

    @Override
    public ChatDto.CreateChatRoomResponse createChatRoomForPersonal(ChatDto.CreateChatRoomRequest chatRoomRequest) {
        Long id = Long.parseLong(securityUtil.getCurrentMemberUsername());
        if (!id.equals(chatRoomRequest.getRoomMakerId())) {
            throw new UserNotFoundException("");
        }

        Long sharedChatRoomId = chatRoomRepository.findSharedChatRoom(chatRoomRequest.getGuestId(), chatRoomRequest.getRoomMakerId());
        if (sharedChatRoomId != null) {
            return new ChatDto.CreateChatRoomResponse(chatRoomRequest.getRoomMakerId(), chatRoomRequest.getGuestId(), sharedChatRoomId);
        }
        Member roomMaker = memberRepository.findById(id).orElseThrow(() -> new UserNotFoundException("유저를 찾을 수 없습니다."));
        Member guest = memberRepository.findById(chatRoomRequest.getGuestId()).orElseThrow(() -> new UserNotFoundException("유저를 찾을 수 없습니다"));

        ChatRoom newRoom = new ChatRoom();
        newRoom.addMembers(roomMaker, guest);

        ChatRoom savedRoom = chatRoomRepository.save(newRoom);

        return new ChatDto.CreateChatRoomResponse(roomMaker.getId(), guest.getId(), savedRoom.getId());
    }

    @Override
    public ChatDto.ChatRoomInfoResponse chatRoomInfo(long roomId, int page, int size) {
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new ChatRoomNotFoundException("채팅방을 찾을 수 없습니다"));
        ChatDto.ChatRoomInfoResponse chatRoomInfoResponse = new ChatDto.ChatRoomInfoResponse(chatRoom);

        Set<ChatUserInfoDto> chatRoomMembers = chatRoomInfoResponse.getChatRoomMembers();
        Long currentRoomId = Long.parseLong(securityUtil.getCurrentMemberUsername());
        if (!chatRoomMembers.contains(new ChatUserInfoDto(memberRepository.findById(currentRoomId).get()))) {
            throw new ChatRoomAccessDeniedException("권한이 없습니다.");
        }

        List<ChatMessage> latestChatMessages = findChatMessagesWithPaging(page, size, roomId);
        List<ChatMessageInfo> chatMessageInfos = latestChatMessages.stream()
                .map(ChatMessageInfo::new)
                .collect(Collectors.toList());

        chatRoomInfoResponse.setLatestChatMessages(chatMessageInfos);
        return chatRoomInfoResponse;
    }

    private List<ChatRoom> findChatRoomsWithPaging(int page, int size, String userId) {
        Sort sort = Sort.by("lastChatMesg.createdAt").descending();
        PageRequest pageRequest = PageRequest.of(page - 1, size, sort);

        List<ChatRoom> chatRooms = chatRoomRepository.findByChatRoomMembersId(userId, pageRequest).getContent();

        return chatRooms;
    }

    public List<ChatMessage> findChatMessagesWithPaging(int page, int size, long roomId) {
        Sort sort = Sort.by("createdAt").descending();
        PageRequest pageRequest = PageRequest.of(page - 1, size, sort);

        List<ChatMessage> result = chatMessageRepository.findListsByRoomId(roomId, pageRequest).getContent();

        return result;
    }

    @Override
    public ChatRoom findById(long chatRoomId) {
        return chatRoomRepository.findById(chatRoomId).orElseThrow();
    }

    @Override
    public ChatDto.ChatRoomListResponse getChatRoomList(int page, int size) {
        return null;
    }
}
