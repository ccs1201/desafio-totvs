package br.com.ccs.contaspagar.api.v1;

import br.com.ccs.contaspagar.domain.entity.Conta;
import br.com.ccs.contaspagar.domain.service.ContaService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/contas")
@RequiredArgsConstructor
class ContaController {
    private final ContaService contaService;
    
    @PostMapping
    public Conta criarConta(@RequestBody Conta conta) {
        return contaService.salvar(conta);
    }
    
    @GetMapping
    public List<Conta> listarContas() {
        return contaService.listarTodas();
    }
    
    @GetMapping("/{id}")
    public Conta buscarConta(@PathVariable Long id) {
        return contaService.buscarPorId(id);
    }
}
