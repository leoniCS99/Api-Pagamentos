package br.com.food.pagamentos.controller;

import br.com.food.pagamentos.dto.PagamentoDto;
import br.com.food.pagamentos.service.PagamentoService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.net.URI;

@RestController
@RequestMapping("/pagamentos")
public class PagamentoController {

    @Autowired
    private PagamentoService pagamentoService;

    @GetMapping
    public Page<PagamentoDto> listar(@PageableDefault(size = 10) Pageable paginacao) {
        return pagamentoService.obterTodos(paginacao);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PagamentoDto> detalhar(@PathVariable @NotNull Long id){
        PagamentoDto pagamentoDto = pagamentoService.obterPorId(id);

        return ResponseEntity.ok(pagamentoDto);

    }

    @PostMapping
    public ResponseEntity<PagamentoDto> cadastrar(@RequestBody @Valid PagamentoDto pagamentoDto, UriComponentsBuilder uriBuilder) {
        PagamentoDto pagamento = pagamentoService.criarPagamento(pagamentoDto);
        URI endereco = uriBuilder.path("/pagamentos/{id}").buildAndExpand(pagamento.getId()).toUri();

        return ResponseEntity.created(endereco).body(pagamento);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PagamentoDto> atualizar(@PathVariable @NotNull Long id, @RequestBody @Valid PagamentoDto pagamentoDto) {
        PagamentoDto atualizado = pagamentoService.atualizarPagamento(id, pagamentoDto);
        return ResponseEntity.ok(atualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<PagamentoDto> remover(@PathVariable @NotNull Long id){
        pagamentoService.excluirPagamento(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/confirmar")
    @CircuitBreaker(name = "atualizaPedido", fallbackMethod = "pagamentoAutorizadoComIntegracaoPendente")
    public void confirmarPagamento(@PathVariable @NotNull Long id){
        pagamentoService.confirmarPagamento(id);
    }

    public void pagamentoAutorizadoComIntegracaoPendente(Long id, Exception e){
        pagamentoService.alteraStatus(id);
    }


}
