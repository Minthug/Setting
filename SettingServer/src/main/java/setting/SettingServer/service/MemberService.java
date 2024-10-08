package setting.SettingServer.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import setting.SettingServer.common.exception.UserNotFoundException;
import setting.SettingServer.dto.MemberDto;
import setting.SettingServer.repository.MemberRepository;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository; // PostgreSQL
    private final RedisTemplate<String, MemberDto> redisTemplate; // Redis
    private final GcpStorageService gcpStorageService;
    private final PasswordEncoder encoder;


    @Cacheable(cacheNames = "memberCache", key = "#id", unless = "#result == null")
    @Transactional(readOnly = true)
    public MemberDto findMember(Long id) {
        String cacheKey = "member: " + id;

        MemberDto cachedMember = redisTemplate.opsForValue().get(cacheKey);
        if (cachedMember != null) {
            return cachedMember;
        }

        return memberRepository.findById(id)
                .map(member -> {
                    MemberDto dto = MemberDto.toDto(member);
                    redisTemplate.opsForValue().set(cacheKey, dto,1, TimeUnit.HOURS);
                    return dto;
                })
                .orElseThrow(() -> new UserNotFoundException("Member not found with id: " + id));
    }

    @Cacheable(cacheNames = "allMembersCache", unless = "#result.isEmpty()")
    @Transactional(readOnly = true)
    public List<MemberDto> findAllMember() {
        String cacheKey = "allMembers";

        List<MemberDto> cachedMembers = (List<MemberDto>) redisTemplate.opsForValue().get(cacheKey);
        if (cachedMembers != null && !cachedMembers.isEmpty()) {
            return cachedMembers;
        }

        List<MemberDto> members = memberRepository.findAll().stream()
                .map(MemberDto::toDto)
                .collect(Collectors.toList());

        if (!members.isEmpty()) {
            redisTemplate.opsForValue().set(cacheKey, (MemberDto) members, 1L, TimeUnit.HOURS);

        }
            return members;
    }
}
