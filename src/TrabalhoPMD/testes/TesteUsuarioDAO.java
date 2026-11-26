package TrabalhoPMD.testes;

import TrabalhoPMD.modelo.Perfil;
import TrabalhoPMD.modelo.Usuario;

/** Testa as operações do EntidadeDAO<Usuario>. */
public class TesteUsuarioDAO extends TesteDAOBase<Usuario> {

    public static void main(String[] args) throws Exception {
        new TesteUsuarioDAO().executar();
    }

    @Override
    protected Class<Usuario> tipoEntidade() {
        return Usuario.class;
    }

    @Override
    protected Usuario criarEntidade(int id, String valor) {
        return new Usuario(id, valor, valor + "@teste.com", "senha-teste", Perfil.USUARIO);
    }

    @Override
    protected String obterValor(Usuario usuario) {
        return usuario.getNome();
    }

    @Override
    protected String nomeTeste() {
        return "usuario";
    }
}
