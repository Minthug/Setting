package setting.SettingServer.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import setting.SettingServer.entity.Member;
import setting.SettingServer.repository.MemberRepository;

import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository; // PostgreSQL

    private RedisTemplate<String, Member> redisTemplate; // Redis

    public Member getMember(Long id) {
        String key = "Member:" + id;
        Member member = redisTemplate.opsForValue().get(key);
        if (member == null) {
            member = memberRepository.findById(id).orElse(null);
            if (member != null) {
                redisTemplate.opsForValue().set(key, member, 1, TimeUnit.HOURS);
            }
        }
        return member;
    }
}
