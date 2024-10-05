package setting.SettingServer.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import setting.SettingServer.common.exception.UserNotFoundException;
import setting.SettingServer.dto.MemberDto;
import setting.SettingServer.entity.Member;
import setting.SettingServer.repository.MemberRepository;

import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository; // PostgreSQL
    private RedisTemplate<String, MemberDto> redisTemplate; // Redis
    private final GcpStorageService gcpStorageService;
    private final PasswordEncoder encoder;

    public MemberService(MemberRepository memberRepository, RedisTemplate<String, MemberDto> redisTemplate, GcpStorageService gcpStorageService, PasswordEncoder encoder) {
        this.memberRepository = memberRepository;
        this.redisTemplate = redisTemplate;
        this.gcpStorageService = gcpStorageService;
        this.encoder = encoder;
    }

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
}
