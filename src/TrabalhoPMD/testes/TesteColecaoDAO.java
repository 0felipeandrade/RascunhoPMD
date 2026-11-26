package TrabalhoPMD.testes;

import TrabalhoPMD.modelo.Colecao;
import TrabalhoPMD.modelo.Usuario;

/** Testa as operações do EntidadeDAO<Colecao>. */
public class TesteColecaoDAO extends TesteDAOBase<Colecao> {

    public static void main(String[] args) throws Exception {
        new TesteColecaoDAO().executar();
    }

    @Override
    protected Class<Colecao> tipoEntidade() {
        return Colecao.class;
    }

    @Override
    protected Colecao criarEntidade(int id, String valor) {
        Usuario usuario = new Usuario(1, "Usuario de teste",
                "usuario@teste.com", "senha-teste");
        return new Colecao(id, usuario, valor);
    }

    @Override
    protected String obterValor(Colecao colecao) {
        return colecao.getNome();
    }

    @Override
    protected String nomeTeste() {
        return "colecao";
    }
}
