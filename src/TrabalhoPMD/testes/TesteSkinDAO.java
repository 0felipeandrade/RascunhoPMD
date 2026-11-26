package TrabalhoPMD.testes;

import TrabalhoPMD.modelo.Skin;

/** Testa as operações do EntidadeDAO<Skin>. */
public class TesteSkinDAO extends TesteDAOBase<Skin> {

    public static void main(String[] args) throws Exception {
        new TesteSkinDAO().executar();
    }

    @Override
    protected Class<Skin> tipoEntidade() {
        return Skin.class;
    }

    @Override
    protected Skin criarEntidade(int id, String valor) {
        return new Skin(id, valor, "Valorant", "Premium", null,
                "https://exemplo.com/skin.png");
    }

    @Override
    protected String obterValor(Skin skin) {
        return skin.getNome();
    }

    @Override
    protected String nomeTeste() {
        return "skin";
    }
}
