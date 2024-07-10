package br.com.fiap.mslogin.controller;

import br.com.fiap.mslogin.config.TokenService;
import br.com.fiap.mslogin.config.DadosTokenJWT;
import br.com.fiap.mslogin.model.Usuario;
import br.com.fiap.mslogin.model.dto.UsuarioRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/login")
public class UsuarioController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private TokenService tokenService;

    @PostMapping
    public ResponseEntity efetuarLogin(@RequestBody @Valid UsuarioRequest usuarioRequest){
        var authenticationToken= new UsernamePasswordAuthenticationToken(usuarioRequest.login(), usuarioRequest.senha());
        var authentication = authenticationManager.authenticate(authenticationToken);
        var token = tokenService.gerarToken((Usuario) authentication.getPrincipal());
        return ResponseEntity.ok(new DadosTokenJWT(token));
    }

}
