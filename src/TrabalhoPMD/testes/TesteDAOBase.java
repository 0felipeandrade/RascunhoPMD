package TrabalhoPMD.testes;

import TrabalhoPMD.dados.EntidadeDAO;
import TrabalhoPMD.dados.PersistenceException;
import TrabalhoPMD.modelo.Entidade;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Reúne os oito cenários solicitados para testar as operações do EntidadeDAO.
 *
 * Cada cenário usa um arquivo temporário próprio. Assim, os testes não
 * modificam os arquivos de dados usados pela aplicação.
 */
abstract class TesteDAOBase<E extends Entidade> {

    @FunctionalInterface
    private interface Operacao {
        
        void executar() throws PersistenceException;

    }

    private Path diretorioTemporario;
    private int numeroArquivo = 0;

    protected abstract Class<E> tipoEntidade();

    protected abstract E criarEntidade(int id, String valor);

    protected abstract String obterValor(E entidade);

    protected abstract String nomeTeste();

    public final void executar() throws Exception {
        
        diretorioTemporario = Files.createTempDirectory("teste-" + nomeTeste() + "-");

        try {
            System.out.println("\n========================================");
            System.out.println("Testes de " + tipoEntidade().getSimpleName() + "DAO");
            System.out.println("========================================");

            testarSalvarIdNovo();
            testarSalvarIdDuplicado();
            testarAtualizarIdInexistente();
            testarAtualizarIdExistente();
            testarApagarIdInexistente();
            testarApagarIdExistente();
            testarBuscarIdInexistente();
            testarBuscarIdExistente();

            System.out.println("[OK] Todos os testes de " + tipoEntidade().getSimpleName() + " passaram.");
        } finally {
            limparArquivosTemporarios();
        }
    }

    private EntidadeDAO<E> novoDAO(String caso) {
        String arquivo = diretorioTemporario.resolve(
                (++numeroArquivo) + "-" + caso + ".txt"
        ).toString();

        return new EntidadeDAO<>(tipoEntidade(), arquivo);
    }

    private void testarSalvarIdNovo() throws Exception {
        EntidadeDAO<E> dao = novoDAO("salvar-id-novo");
        E entidade = criarEntidade(10, "original");

        executarOperacao(() -> dao.salvar(entidade));

        E encontrada = executarBusca(dao, 10);
        verificar(encontrada.getId() == 10, "salvar: ID novo foi inserido");
        verificar("original".equals(obterValor(encontrada)),
                "salvar: os dados da entidade inserida foram preservados");
        verificarQuantidade(dao, 1, "salvar: existe exatamente uma entidade");
    }

    private void testarSalvarIdDuplicado() throws Exception {
        EntidadeDAO<E> dao = novoDAO("salvar-id-duplicado");
        dao.salvar(criarEntidade(20, "original"));

        esperarExcecao(() -> dao.salvar(criarEntidade(20, "duplicada")),
                "SALVAR", "salvar: ID duplicado provoca exceção");

        E encontrada = executarBusca(dao, 20);
        verificar("original".equals(obterValor(encontrada)),
                "salvar: a entidade original não foi substituída");
        verificarQuantidade(dao, 1, "salvar: a tentativa duplicada não inseriu outra entidade");
    }

    private void testarAtualizarIdInexistente() throws Exception {
        EntidadeDAO<E> dao = novoDAO("atualizar-id-inexistente");

        esperarExcecao(() -> dao.atualizar(criarEntidade(30, "nao existe")),
                "ATUALIZAR", "atualizar: ID inexistente provoca exceção");

        verificarVazio(dao, "atualizar: nada foi inserido quando o ID não existia");
    }

    private void testarAtualizarIdExistente() throws Exception {
        EntidadeDAO<E> dao = novoDAO("atualizar-id-existente");
        dao.salvar(criarEntidade(40, "antes"));

        dao.atualizar(criarEntidade(40, "depois"));

        E atualizada = executarBusca(dao, 40);
        verificar("depois".equals(obterValor(atualizada)),
                "atualizar: os dados da entidade foram alterados");
        verificarQuantidade(dao, 1, "atualizar: continua existindo apenas uma entidade");
    }

    private void testarApagarIdInexistente() throws Exception {
        EntidadeDAO<E> dao = novoDAO("apagar-id-inexistente");
        dao.salvar(criarEntidade(50, "original"));

        esperarExcecao(() -> dao.apagar(999),
                "APAGAR", "apagar: ID inexistente provoca exceção");

        E encontrada = executarBusca(dao, 50);
        verificar("original".equals(obterValor(encontrada)),
                "apagar: a entidade existente não foi alterada");
        verificarQuantidade(dao, 1, "apagar: nada foi removido ao informar ID inexistente");
    }

    private void testarApagarIdExistente() throws Exception {
        EntidadeDAO<E> dao = novoDAO("apagar-id-existente");
        dao.salvar(criarEntidade(60, "remover"));

        E removida = dao.apagar(60);

        verificar(removida != null, "apagar: o método retornou a entidade removida");
        verificar(removida.getId() == 60, "apagar: o retorno contém o ID esperado");
        verificar("remover".equals(obterValor(removida)),
                "apagar: o retorno contém os dados da entidade removida");
        esperarExcecao(() -> dao.buscar(60),
                "BUSCAR", "apagar: a entidade não existe mais após a remoção");
        verificarVazio(dao, "apagar: o conjunto ficou vazio");
    }

    private void testarBuscarIdInexistente() throws Exception {
        EntidadeDAO<E> dao = novoDAO("buscar-id-inexistente");

        esperarExcecao(() -> dao.buscar(70),
                "BUSCAR", "buscar: ID inexistente provoca exceção");
        verificarVazio(dao, "buscar: a tentativa não alterou o conjunto");
    }

    private void testarBuscarIdExistente() throws Exception {
        EntidadeDAO<E> dao = novoDAO("buscar-id-existente");
        dao.salvar(criarEntidade(80, "encontrada"));

        E encontrada = dao.buscar(80);

        verificar(encontrada != null, "buscar: o método retornou uma entidade");
        verificar(encontrada.getId() == 80, "buscar: o retorno tem o ID esperado");
        verificar("encontrada".equals(obterValor(encontrada)),
                "buscar: o retorno contém os dados esperados");
    }

    private E executarBusca(EntidadeDAO<E> dao, int id) {
        try {
            return dao.buscar(id);
        } catch (PersistenceException e) {
            throw new AssertionError("Não foi possível buscar a entidade " + id, e);
        }
    }

    private void executarOperacao(Operacao operacao) {
        try {
            operacao.executar();
        } catch (PersistenceException e) {
            throw new AssertionError("A operação não deveria lançar exceção: " + e.getMessage(), e);
        }
    }

    private void esperarExcecao(Operacao operacao, String operacaoEsperada,
                                String descricao) {
        try {
            operacao.executar();
            throw new AssertionError("[FALHOU] " + descricao + ": nenhuma exceção ocorreu");
        } catch (PersistenceException e) {
            verificar(operacaoEsperada.equals(e.getOperacao()),
                    descricao + " (operação informada pela exceção)");
        }
    }

    private void verificarQuantidade(EntidadeDAO<E> dao, int quantidade,
                                     String descricao) {
        try {
            E[] entidades = dao.carregar();
            verificar(entidades.length == quantidade, descricao);
        } catch (PersistenceException e) {
            if (quantidade == 0 && "CARREGAR TODOS".equals(e.getOperacao())) {
                verificar(true, descricao);
            } else {
                throw new AssertionError("Não foi possível conferir a quantidade de entidades", e);
            }
        }
    }

    private void verificarVazio(EntidadeDAO<E> dao, String descricao) {
        try {
            E[] entidades = dao.carregar();
            throw new AssertionError("[FALHOU] " + descricao
                    + ": eram esperadas zero entidades, mas foram encontradas "
                    + entidades.length);
        } catch (PersistenceException e) {
            verificar("CARREGAR TODOS".equals(e.getOperacao()), descricao);
        }
    }

    private void verificar(boolean condicao, String descricao) {
        if (!condicao) {
            throw new AssertionError("[FALHOU] " + descricao);
        }
        System.out.println("[OK] " + descricao);
    }

    private void limparArquivosTemporarios() throws IOException {
        try (DirectoryStream<Path> arquivos =
                     Files.newDirectoryStream(diretorioTemporario)) {
            for (Path arquivo : arquivos) {
                Files.deleteIfExists(arquivo);
            }
        }
        Files.deleteIfExists(diretorioTemporario);
    }
}
