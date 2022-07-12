package br.com.food.pagamentos.service;

import br.com.food.pagamentos.dto.PagamentoDto;
import br.com.food.pagamentos.model.Pagamento;
import br.com.food.pagamentos.enums.Status;
import br.com.food.pagamentos.repository.PagamentoRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;

@Service
public class PagamentoService {
    @Autowired
    private PagamentoRepository pagamentoRepository;
    @Autowired
    private ModelMapper modelMapper;

    public Page<PagamentoDto> obterTodos(Pageable paginacao){
        return pagamentoRepository
                .findAll(paginacao)
                .map(p -> modelMapper.map(p,PagamentoDto.class));
    }

    public PagamentoDto obterPorId(Long id){
        Pagamento pagamento = pagamentoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException());
        return modelMapper.map(pagamento, PagamentoDto.class);
    }

    public  PagamentoDto criarPagamento(PagamentoDto pagamentoDto){
        Pagamento pagamento = modelMapper.map(pagamentoDto, Pagamento.class);
        pagamento.setStatus(Status.CRIADO);
        pagamentoRepository.save(pagamento);

        return modelMapper.map(pagamento, PagamentoDto.class);

    }

    public PagamentoDto atualizarPagamento(Long id, PagamentoDto pagamentoDto){
        Pagamento pagamento = modelMapper.map(pagamentoDto, Pagamento.class);
        pagamento.setId(id);
        pagamento = pagamentoRepository.save(pagamento);

        return modelMapper.map(pagamento, PagamentoDto.class);
    }

    public void excluirPagamento(Long id){
        pagamentoRepository.deleteById(id);
    }


}
