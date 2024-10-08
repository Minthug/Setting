package setting.SettingServer.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import setting.SettingServer.dto.MemberDto;
import setting.SettingServer.service.MemberService;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/members")
public class MemberController {

    private final MemberService memberService;


//    @GetMapping("")
//    public String getUserInfo(@AuthenticationPrincipal UserDetails userDetails) {
//        return userDetails.getUsername();
//    }

    @GetMapping("/{id}")
    public ResponseEntity<MemberDto> findMember(@PathVariable(value = "id") Long id) {
        MemberDto member = memberService.findMember(id);
        return ResponseEntity.ok(member);
    }

    @GetMapping("")
    public ResponseEntity<List<MemberDto>> findAllMember() {
        List<MemberDto> members = memberService.findAllMember();
        return ResponseEntity.ok(members);
    }
}
