package TrabalhoPMD.visao;

import TrabalhoPMD.dados.LeitorDados;
import TrabalhoPMD.modelo.Pergunta;
import TrabalhoPMD.modelo.Usuario;
import TrabalhoPMD.servico.ServicoQuestionario;
import java.awt.*;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class TelaQuestionario extends JFrame {

    private Usuario usuario;
    private Runnable salvarUsuario;

    private ServicoQuestionario servico;
    private List<Pergunta> perguntas;

    private int indiceAtual = 0;
    private int acertos = 0;
    private int moedasGanhos = 0;

    private boolean respondida = false;
    private boolean finalizado = false;

    // Componentes
    private JLabel lblSaldo;
    private JLabel lblProgresso;
    private JLabel lblNumero;
    private JLabel lblFeedback;

    private JTextArea txtPergunta;

    private JProgressBar barraProgresso;

    private JPanel painelAlternativas;

    private JRadioButton[] alternativas;
    private ButtonGroup grupoAlternativas;

    private JButton btnAcao;

    // Cores
    private final Color FUNDO = new Color(20, 23, 34);
    private final Color CARTAO = new Color(31, 36, 51);
    private final Color OPCAO = new Color(43, 49, 67);
    private final Color DESTAQUE = new Color(145, 112, 235);
    private final Color DOURADO = new Color(233, 196, 106);
    private final Color TEXTO = new Color(240, 242, 248);
    private final Color SECUNDARIA = new Color(157, 165, 182);

    private final char[] letras = {'A', 'B', 'C', 'D'};

    // Construtor simples (sem persistência automática)
    public TelaQuestionario(Usuario usuario) {
        this(usuario, null);
    }

    // Construtor com ação para salvar o usuário
    public TelaQuestionario(Usuario usuario,Runnable salvarUsuario) {

        this.usuario = usuario;
        this.salvarUsuario = salvarUsuario;

        setTitle("Skin Collection - Questionário");
        setSize(850, 700);
        setMinimumSize(new Dimension(750, 620));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        montarInterface();
        carregarQuestionario();
    }

    // Monta toda a interface
    private void montarInterface() {

        JPanel principal = new JPanel(new BorderLayout(0, 22));
        principal.setBackground(FUNDO);
        principal.setBorder(new EmptyBorder(25, 35, 25, 35));

        // CABEÇALHO
        JPanel cabecalho = new JPanel(new BorderLayout());
        cabecalho.setOpaque(false);

        JPanel identificacao = new JPanel();
        identificacao.setLayout(
                new BoxLayout(identificacao, BoxLayout.Y_AXIS)
        );
        identificacao.setOpaque(false);

        JLabel titulo = new JLabel("SKIN COLLECTION");
        titulo.setFont(new Font("Arial", Font.BOLD, 23));
        titulo.setForeground(TEXTO);

        JLabel subtitulo = new JLabel("CENTRAL DE DESAFIOS");
        subtitulo.setFont(new Font("Arial", Font.PLAIN, 11));
        subtitulo.setForeground(SECUNDARIA);

        identificacao.add(titulo);
        identificacao.add(Box.createVerticalStrut(5));
        identificacao.add(subtitulo);

        lblSaldo = new JLabel();
        lblSaldo.setFont(new Font("Arial", Font.BOLD, 16));
        lblSaldo.setForeground(DOURADO);

        cabecalho.add(identificacao, BorderLayout.WEST);
        cabecalho.add(lblSaldo, BorderLayout.EAST);

        principal.add(cabecalho, BorderLayout.NORTH);

        // ÁREA CENTRAL
        JPanel centro = new JPanel(new BorderLayout(0, 20));
        centro.setOpaque(false);

        // PROGRESSO
        JPanel painelProgresso = new JPanel(new BorderLayout(0, 9));
        painelProgresso.setOpaque(false);

        lblProgresso = new JLabel("PERGUNTA 1/10");
        lblProgresso.setFont(new Font("Arial", Font.BOLD, 12));
        lblProgresso.setForeground(SECUNDARIA);

        barraProgresso = new JProgressBar();
        barraProgresso.setMinimum(0);
        barraProgresso.setMaximum(10);
        barraProgresso.setValue(0);
        barraProgresso.setPreferredSize(new Dimension(100, 8));
        barraProgresso.setForeground(DESTAQUE);
        barraProgresso.setBackground(OPCAO);
        barraProgresso.setBorderPainted(false);

        painelProgresso.add(lblProgresso, BorderLayout.NORTH);
        painelProgresso.add(barraProgresso, BorderLayout.CENTER);

        centro.add(painelProgresso, BorderLayout.NORTH);

        // CARTÃO DA PERGUNTA
        JPanel cartao = new JPanel();
        cartao.setLayout(
                new BoxLayout(cartao, BoxLayout.Y_AXIS)
        );
        cartao.setBackground(CARTAO);
        cartao.setBorder(new EmptyBorder(28, 30, 28, 30));

        lblNumero = new JLabel("CONHECIMENTOS GERAIS");
        lblNumero.setFont(new Font("Arial", Font.BOLD, 11));
        lblNumero.setForeground(DESTAQUE);
        lblNumero.setAlignmentX(Component.LEFT_ALIGNMENT);

        txtPergunta = new JTextArea();
        txtPergunta.setFont(new Font("Arial", Font.BOLD, 23));
        txtPergunta.setForeground(TEXTO);
        txtPergunta.setBackground(CARTAO);
        txtPergunta.setLineWrap(true);
        txtPergunta.setWrapStyleWord(true);
        txtPergunta.setEditable(false);
        txtPergunta.setFocusable(false);
        txtPergunta.setBorder(null);
        txtPergunta.setRows(3);
        txtPergunta.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel instrucao = new JLabel(
                "Selecione uma das alternativas abaixo:"
        );
        instrucao.setFont(new Font("Arial", Font.PLAIN, 13));
        instrucao.setForeground(SECUNDARIA);
        instrucao.setAlignmentX(Component.LEFT_ALIGNMENT);

        // ALTERNATIVAS
        painelAlternativas = new JPanel(
                new GridLayout(4, 1, 0, 12)
        );
        painelAlternativas.setOpaque(false);

        grupoAlternativas = new ButtonGroup();
        alternativas = new JRadioButton[4];

        for (int i = 0; i < 4; i++) {

            alternativas[i] = new JRadioButton();

            alternativas[i].setFont(
                    new Font("Arial", Font.PLAIN, 15)
            );

            alternativas[i].setForeground(TEXTO);
            alternativas[i].setBackground(OPCAO);
            alternativas[i].setOpaque(true);
            alternativas[i].setFocusPainted(false);

            alternativas[i].setBorder(
                    new EmptyBorder(13, 15, 13, 15)
            );

            alternativas[i].setCursor(
                    new Cursor(Cursor.HAND_CURSOR)
            );

            grupoAlternativas.add(alternativas[i]);
            painelAlternativas.add(alternativas[i]);
        }

        // RECOMPENSA
        lblFeedback = new JLabel(" ");
        lblFeedback.setFont(
                new Font("Arial", Font.BOLD, 13)
        );
        lblFeedback.setForeground(SECUNDARIA);
        lblFeedback.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel recompensa = new JLabel(
                "RECOMPENSA:  MOEDAS POR ACERTO"
        );
        recompensa.setFont(
                new Font("Arial", Font.BOLD, 11)
        );
        recompensa.setForeground(DOURADO);
        recompensa.setAlignmentX(Component.LEFT_ALIGNMENT);

        cartao.add(lblNumero);
        cartao.add(Box.createVerticalStrut(20));
        cartao.add(txtPergunta);
        cartao.add(Box.createVerticalStrut(15));
        cartao.add(instrucao);
        cartao.add(Box.createVerticalStrut(20));
        cartao.add(painelAlternativas);
        cartao.add(Box.createVerticalStrut(22));
        cartao.add(recompensa);
        cartao.add(Box.createVerticalStrut(12));
        cartao.add(lblFeedback);

        centro.add(cartao, BorderLayout.CENTER);

        principal.add(centro, BorderLayout.CENTER);

        // BOTÃO INFERIOR
        JPanel rodape = new JPanel(new BorderLayout());
        rodape.setOpaque(false);

        JLabel aviso = new JLabel(
                "Acerte perguntas e acumule moedas!"
        );
        aviso.setFont(new Font("Arial", Font.PLAIN, 12));
        aviso.setForeground(SECUNDARIA);

        btnAcao = new JButton("RESPONDER");
        btnAcao.setFont(new Font("Arial", Font.BOLD, 13));
        btnAcao.setForeground(Color.WHITE);
        btnAcao.setBackground(DESTAQUE);
        btnAcao.setFocusPainted(false);
        btnAcao.setBorderPainted(false);
        btnAcao.setCursor(
                new Cursor(Cursor.HAND_CURSOR)
        );
        btnAcao.setPreferredSize(new Dimension(190, 48));

        btnAcao.addActionListener(e -> executarAcao());

        rodape.add(aviso, BorderLayout.WEST);
        rodape.add(btnAcao, BorderLayout.EAST);

        principal.add(rodape, BorderLayout.SOUTH);

        setContentPane(principal);
    }

    // Carrega as perguntas do arquivo TXT
   private void carregarQuestionario() {

    try {

        LeitorDados leitor = new LeitorDados();

        Path caminho = Path.of("src/TrabalhoPMD/dados","dados_Questionario.txt").toAbsolutePath();

        System.out.println("==============================");
        System.out.println("DIAGNÓSTICO QUESTIONÁRIO");
        System.out.println("==============================");

        System.out.println(
            "Diretório atual: " + System.getProperty("user.dir")
        );

        System.out.println(
            "Caminho do arquivo: " + caminho
        );

        System.out.println(
            "Arquivo encontrado: " + java.nio.file.Files.exists(caminho)
        );

        if (!java.nio.file.Files.isRegularFile(caminho)) {
            throw new IOException(
                "Arquivo TXT não encontrado: " + caminho
            );
        }

        List<Pergunta> todas = leitor.carregarPerguntas(caminho);

        System.out.println("Perguntas carregadas: " + todas.size());

        if (todas.isEmpty()) {

            throw new IOException(
                "O arquivo existe, mas não possui perguntas válidas."
            );
        }

        servico = new ServicoQuestionario(todas);

        perguntas = servico.sortearPerguntas(Math.min(10, todas.size()));

        mostrarPergunta();

        System.out.println("Questionário carregado!");

    } catch (IOException | RuntimeException e) {

        e.printStackTrace();

        txtPergunta.setText(
            "Não foi possível carregar o questionário."
        );

        lblFeedback.setText(e.toString());

        painelAlternativas.setVisible(false);
        btnAcao.setEnabled(false);

        lblProgresso.setText("ERRO AO CARREGAR");
    }
}

    // Apresenta a pergunta atual
    private void mostrarPergunta() {

        Pergunta pergunta = perguntas.get(indiceAtual);

        lblNumero.setText(
                "QUESTÃO " + (indiceAtual + 1)
        );

        txtPergunta.setText(pergunta.getEnunciado());

        lblProgresso.setText(
                "PERGUNTA " + (indiceAtual + 1)
                + "/" + perguntas.size()
        );

        barraProgresso.setMaximum(perguntas.size());
        barraProgresso.setValue(indiceAtual);

        grupoAlternativas.clearSelection();

        for (int i = 0; i < 4; i++) {

            String texto = pergunta.getAlternativas().get(letras[i]);

            alternativas[i].setText(letras[i] + "  -  " + texto);

            alternativas[i].setActionCommand(String.valueOf(letras[i]));

            alternativas[i].setEnabled(true);
        }

        lblFeedback.setText(" ");

        lblSaldo.setText(
                "◈ " + usuario.getMoedas() + " MOEDAS"
        );

        respondida = false;

        btnAcao.setText("RESPONDER");
        btnAcao.setEnabled(true);
    }

    // Controla o botão principal
    private void executarAcao() {

        if (finalizado) {
            dispose();
            return;
        }

        if (!respondida) {

            corrigirResposta();

        } else {

            if (indiceAtual < perguntas.size() - 1) {

                indiceAtual++;
                mostrarPergunta();

            } else {

                mostrarResultado();
            }
        }
    }

    // Verifica a resposta selecionada
    private void corrigirResposta() {

        if (grupoAlternativas.getSelection() == null) {

            lblFeedback.setText(
                    "Selecione uma alternativa primeiro."
            );

            return;
        }

        Pergunta pergunta = perguntas.get(indiceAtual);

        char resposta = grupoAlternativas.getSelection()
                .getActionCommand().charAt(0);

        int recompensa = servico.verificarResposta(
                pergunta,
                resposta
        );

        if (recompensa > 0) {

            acertos++;
            moedasGanhos += recompensa;

            usuario.adicionarMoedas(recompensa);

            lblFeedback.setForeground(
                    new Color(91, 205, 143)
            );

            lblFeedback.setText(
                    "Correto! Você ganhou "
                    + recompensa + " moedas!"
            );

            lblSaldo.setText(
                    "◈ " + usuario.getMoedas() + " MOEDAS"
            );

            // Salva o usuário, se uma ação de persistência foi fornecida.
            if (salvarUsuario != null) {

                try {
                    salvarUsuario.run();

                } catch (RuntimeException e) {

                    JOptionPane.showMessageDialog(
                            this,
                            "As moedas foram adicionadas em memória, "
                            + "mas houve um erro ao salvar o usuário."
                    );
                }
            }

        } else {

            char correta = pergunta.getRespostaCorreta();

            lblFeedback.setForeground(
                    new Color(238, 112, 112)
            );

            lblFeedback.setText(
                    "Incorreto! Resposta certa: "
                    + correta + " - "
                    + pergunta.getAlternativas().get(correta)
            );
        }

        for (JRadioButton opcao : alternativas) {
            opcao.setEnabled(false);
        }

        respondida = true;

        if (indiceAtual == perguntas.size() - 1) {
            btnAcao.setText("VER RESULTADO");
        } else {
            btnAcao.setText("PRÓXIMA PERGUNTA");
        }
    }

    // Mostra o resultado da rodada
    private void mostrarResultado() {

        finalizado = true;

        painelAlternativas.setVisible(false);

        lblNumero.setText("DESAFIO CONCLUÍDO");

        txtPergunta.setText(
                "Parabéns! Você concluiu o questionário."
        );

        lblProgresso.setText("100% CONCLUÍDO");

        barraProgresso.setValue(perguntas.size());

        lblFeedback.setForeground(DOURADO);

        lblFeedback.setText(
                "<html>Acertos: " + acertos
                + " de " + perguntas.size()
                + "<br>Moedas conquistadas: +"
                + moedasGanhos + "</html>"
        );

        btnAcao.setText("FINALIZAR");

    }
}