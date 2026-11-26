package TrabalhoPMD.testes;

import TrabalhoPMD.modelo.Avaliacao;
import TrabalhoPMD.modelo.Skin;
import TrabalhoPMD.modelo.Usuario;

/** Testa as operações do EntidadeDAO<Avaliacao>. */
public class TesteAvaliacaoDAO extends TesteDAOBase<Avaliacao> {

    public static void main(String[] args) throws Exception {
        new TesteAvaliacaoDAO().executar();
    }

    @Override
    protected Class<Avaliacao> tipoEntidade() {
        return Avaliacao.class;
    }

    @Override
    protected Avaliacao criarEntidade(int id, String valor) {
        Usuario usuario = new Usuario(1, "Usuario de teste",
                "usuario@teste.com", "senha-teste");
        Skin skin = new Skin(1, "Skin de teste", "Valorant", "Premium", usuario);
        return new Avaliacao(id, usuario, skin, 8, valor);
    }

    @Override
    protected String obterValor(Avaliacao avaliacao) {
        return avaliacao.getComentario();
    }

    @Override
    protected String nomeTeste() {
        return "avaliacao";
    }
}
