package br.com.omni.projetos.controller.projeto;

import br.com.omni.projetos.dto.projeto.AtualizarProjetoRequest;
import br.com.omni.projetos.dto.projeto.DeletarProjetoRequest;
import br.com.omni.projetos.model.Projeto;
import br.com.omni.projetos.model.Regulatorio;
import br.com.omni.projetos.repository.DepartamentoRepository;
import br.com.omni.projetos.repository.ProjetoRepositoy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("home")
public class HomeController {
    @Autowired
    private ProjetoRepositoy projetoRepositoy;

    @Autowired
    private DepartamentoRepository departamentoRepository;

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    //ctrl alt c cria variavel
    //ctrl alt v cria variavel
    //ctrl alt o elimina imports
    //@Transient faz com que atributo n sera criado do banco de dados


    @GetMapping
    public String home(Long id, Model model) {
        if (id == null) {
            List<Projeto> projetos = projetoRepositoy.findAll();
            model.addAttribute("projetos", projetos);

            return "home";

        } else {
            Optional<Projeto> projetos = projetoRepositoy.findById(id);

            if (projetos.isPresent()) {
                Projeto projeto = projetos.get();
                model.addAttribute("projetos", projeto);

                return "home";
            }

            return "redirect:home";

        }
    }


    @GetMapping("/regulatorio/{regulatorio}")
    public String regularoio(@PathVariable("regulatorio") String regulatorio, Model model) {
        List<Projeto> byRegulatorio = projetoRepositoy.findByRegulatorio(Regulatorio.valueOf(regulatorio.toUpperCase()));
        String status = String.valueOf(Regulatorio.valueOf(regulatorio.toUpperCase()));

        model.addAttribute("regulatorio", status);
        model.addAttribute("projetos", byRegulatorio);

        return "home";
    }

    @GetMapping("/detalhes/{id}")
    public String detalhe(@PathVariable("id") Long id, Model model) {
        Optional<Projeto> projeto = projetoRepositoy.findById(id);

        if (projeto.isPresent()) {
            Projeto proj = projeto.get();
            model.addAttribute("projeto", proj);

            return "projeto/detalhes";
        }

        return "redirect:home";
    }


    @GetMapping("/alterar/{id}")
    public String paginaAtualizar(@PathVariable("id") Long id, Model model) {

        Optional<Projeto> optional = projetoRepositoy.findById(id);
        if(optional.isPresent()) {
            Projeto projeto = optional.get();
            model.addAttribute("projeto", projeto);
            return "projeto/alterar";
        }

        return "home";
    }

    @PostMapping("alterado")
    @Transactional
    public String atualizado(@Valid AtualizarProjetoRequest request, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "redirect:alterar/" + request.getId();
        }

        Projeto projeto = projetoRepositoy.getOne(request.getId());

        projeto.setId(request.getId());
        projeto.setNome(request.getNome());
        projeto.setAnaliseSituacaoAtual(request.getSituacaoAtual());
        projeto.setDescricaoSituacaoDesejada(request.getSituacaoDesejada());
        projeto.setDescricaoSolucao(request.getSolucao());

        return "redirect:/home";
    }

    @PostMapping("deletado")
    @Transactional
    public String deletar(DeletarProjetoRequest request, Model model) {
//        if (result.hasErrors()) {
//            return "/projeto/atualizar";
//        }

        Optional<Projeto> projeto = projetoRepositoy.findById(request.getId());

        if(projeto.isPresent() && request.getSenha().equals("Omni2020")) {
            projetoRepositoy.deleteById(request.getId());
            List<Projeto>      projetos      = projetoRepositoy.findAll();
            model.addAttribute("projetos", projetos);

            return "redirect:/home";
        }

        return "redirect:alterar/" + request.getId();
    }

//    @PutMapping("/atualizar/{id}")
//    @Transactional
//    public String atualizar(@PathVariable("id") Long id, @Valid AtualizarProjetoRequest atualizarProjeto, Model model) {
//        Optional<Projeto> optional = projetoRepositoy.findById(id);
//
//        if(optional.isPresent()) {
//            Projeto proj = optional.get();
//            model.addAttribute("projeto", proj);
//
//            Projeto projeto = atualizarProjeto.atualizar(id, projetoRepositoy);
//            model.addAttribute("projetos", optional);
//
//            return "home";
//        }
//
//        return "home";
//    }
}
