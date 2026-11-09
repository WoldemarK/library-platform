package com.kroon.its.libraryservice.rest;

import com.kroon.its.libraryservice.dto.request.LoanCreateRequest;
import com.kroon.its.libraryservice.dto.response.LoanResponse;
import com.kroon.its.libraryservice.dto.response.PageResponse;
import com.kroon.its.libraryservice.service.LoanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/loans")
public class LoanController {

    private final LoanService loanService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LoanResponse borrowBook(@Valid @RequestBody LoanCreateRequest request,
                                   @AuthenticationPrincipal Jwt jwt) {
        return loanService.borrowBook(request, jwt.getSubject());
    }

    @PostMapping("/{loanId}/return")
    public LoanResponse returnBook(@PathVariable Long loanId, @AuthenticationPrincipal Jwt jwt) {
        return loanService.returnBook(loanId, jwt.getSubject());
    }

    @GetMapping("/my")
    public PageResponse<LoanResponse> getMyLoans(@AuthenticationPrincipal Jwt jwt,
                                                 @RequestParam(defaultValue = "0") int page,
                                                 @RequestParam(defaultValue = "20") int size) {
        return loanService.getMyLoans(jwt.getSubject(), page, size);
    }

    @GetMapping("/{loanId}")
    public LoanResponse getMyLoanById(@PathVariable Long loanId, @AuthenticationPrincipal Jwt jwt) {
        return loanService.getMyLoanById(loanId, jwt.getSubject());
    }
}
