/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JInternalFrame.java to edit this template
 */
package projeto_sc.telas;

import java.sql.*;
import java.util.HashMap;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import projeto_sc.db.ModuloConexao;
import net.proteanit.sql.DbUtils;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.view.JasperViewer;

/**
 *
 * @author ninguem
 */
public class TelaOs extends javax.swing.JInternalFrame {

    Connection conexao = null;
    PreparedStatement pst = null;
    ResultSet rs = null;
    
    private String tipo;

    /**
     * Creates new form TelaOs
     */
    public TelaOs() {
        initComponents();
        conexao = ModuloConexao.conector();
    }
    
    
    private void pesquisarCliente(){
        String sql = "SELECT idcli as ID, nomecliente as Nome, tel_cli as Telefone from tbclientes where nomecliente like ?";
        
        try {
            pst = conexao.prepareStatement(sql);
            pst.setString(1, txtCliPesquisar.getText() + "%");
            rs = pst.executeQuery();
            tblClientes.setModel(DbUtils.resultSetToTableModel(rs));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
            System.out.println(e);
        }
    }
    
    private void setarCampos(){
        int linha_selecionada = tblClientes.getSelectedRow();
        lblCliId.setText(tblClientes.getModel().getValueAt(linha_selecionada, 0).toString());
    }
    
    private void emitir_os(){
        String sql = "INSERT INTO tb_os (tipo, situacao, equipamento, defeito, servico, tecnico, valor, idcli) values (?, ?, ?, ?, ?, ?, ?, ?)";
        
        try {
            pst = conexao.prepareStatement(sql);
            pst.setString(1, tipo);
            pst.setString(2, cbSituacao.getSelectedItem().toString());
            pst.setString(3, txtEquipamento.getText());
            pst.setString(4, txtDefeito.getText());
            pst.setString(5, txtServico.getText());
            pst.setString(6, txtTecnico.getText());
            pst.setString(7, txtValor.getText().replace(",", "."));
            pst.setString(8, lblCliId.getText());
            
            if(txtEquipamento.getText().isEmpty() || txtDefeito.getText().isEmpty() || cbSituacao.getSelectedItem().equals(" ")){
                JOptionPane.showMessageDialog(null, "[ERRO] Preencha os campos obrigatórios!!");
                
            }else{
                int emitido = pst.executeUpdate();
                if(emitido > 0){
                    JOptionPane.showMessageDialog(null, "Ordem de Serviço (OS) Emitida com sucesso!!");
                    //recuperar o número da OS
                    recuperar_os();
                    btnOsAdd.setEnabled(false);
                    btnOsPesquisar.setEnabled(false);
                    btnOsImprimir.setEnabled(true);
                }
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
            System.out.println(e);
        }
    }
    
    private void pesquisar_os(){
        String num_os = JOptionPane.showInputDialog("Número da OS");
        String sql = "select os, date_format(data_os, '%d/%m/%Y - %H:%i'), tipo, situacao, equipamento, defeito, servico, tecnico, valor, idcli from tb_os where os = " + num_os;
        try {
            pst = conexao.prepareStatement(sql);
            rs = pst.executeQuery();
            
            if(rs.next() == true){
                lblOs.setText(rs.getString(1));
                lblData.setText(rs.getString(2));
                
                String rbtTipo = rs.getString(3);
                if(rbtTipo.equals("OS")){
                    rbtOs.setSelected(true);
                    tipo = "OS";
                }else{
                    rbtOrc.setSelected(true);
                    tipo = "Orçamento";
                }
                
                cbSituacao.setSelectedItem(rs.getString(4));
                txtEquipamento.setText(rs.getString(5));
                txtDefeito.setText(rs.getString(6));
                txtServico.setText(rs.getString(7));
                txtTecnico.setText(rs.getString(8));
                txtValor.setText(rs.getString(9));
                lblCliId.setText(rs.getString(10));
                
                //Desabilitando botões
                btnOsAdd.setEnabled(false);
                btnOsPesquisar.setEnabled(false);
                txtCliPesquisar.setEditable(false);
                tblClientes.setVisible(false);
                
                //Ativar demais botões
                btnOsAlterar.setEnabled(true);
                btnOsExcluir.setEnabled(true);
                btnOsImprimir.setEnabled(true);
                
            }else{
                JOptionPane.showMessageDialog(null, "OS não cadastrada.");
            }
        } catch (java.sql.SQLSyntaxErrorException e) {
            JOptionPane.showMessageDialog(null, "OS Inválida");
            System.out.println(e);
        } catch(Exception e2){
            JOptionPane.showMessageDialog(null, e2);
        }
    }
    
    private void alterar_os(){
        String sql = "UPDATE tb_os set tipo = ? , situacao = ?, equipamento = ?, defeito = ?, servico = ?, tecnico = ?, valor = ? where os = ?";
        
        try {
            pst = conexao.prepareStatement(sql);
            pst.setString(1, tipo);
            pst.setString(2, cbSituacao.getSelectedItem().toString());
            pst.setString(3, txtEquipamento.getText());
            pst.setString(4, txtDefeito.getText());
            pst.setString(5, txtServico.getText());
            pst.setString(6, txtTecnico.getText());
            pst.setString(7, txtValor.getText().replace(",", "."));
            pst.setString(8, lblOs.getText());
            
            if(txtEquipamento.getText().isEmpty() || txtDefeito.getText().isEmpty() || cbSituacao.getSelectedItem().equals(" ")){
                JOptionPane.showMessageDialog(null, "[ERRO] Preencha os campos obrigatórios!!");
                
            }else{
                int emitido = pst.executeUpdate();
                if(emitido > 0){
                    JOptionPane.showMessageDialog(null, "Ordem de Serviço (OS) Alterada com sucesso!!");
                    
                    limparCampos();
                    
                }
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
            System.out.println(e);
        }
        
    }
    
    private void excluir_os(){
        int confirma = JOptionPane.showConfirmDialog(null, "Tem certeza que deseja excluir essa OS?", "Atenção", JOptionPane.YES_NO_OPTION);
        
        if(confirma == JOptionPane.YES_OPTION){
            String sql = "DELETE FROM tb_os where os = ?";
            
            try {
                pst = conexao.prepareStatement(sql);
                pst.setString(1, lblOs.getText());
                int apagado = pst.executeUpdate();
                
                if(apagado > 0){
                    JOptionPane.showMessageDialog(null, "OS excluida com sucesso!!");
                   
                    limparCampos();
                    btnOsAdd.setEnabled(true);
        txtCliPesquisar.setEditable(true);
        tblClientes.setVisible(true);
                    
                }
            } catch (Exception e) {
            }
        }
    }
    
    private void imprimir_os(){
        //Imprimindo uma OS.
        int confirma = JOptionPane.showConfirmDialog(null, "Confirma a impressão desta OS?", "Atenção", JOptionPane.YES_NO_OPTION);
        
        if(confirma == JOptionPane.YES_OPTION){
            //Imprimindo relatório com o framework JasperReports
            try {
                //Usando a classe HashMap para criar um Filtro.
                HashMap filtro = new HashMap();
                filtro.put("os", Integer.parseInt(lblOs.getText()));
                //Usando a classe JasperPrint para preparar a impressão de um relatório
                JasperPrint print = JasperFillManager.fillReport("C:/reports/os.jasper", filtro, conexao);
                //A linha abaixo exibe o relatório através da classe JasperViewer
                JasperViewer.viewReport(print, false);
                
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, e);
            }
        }
    }
    
    private void recuperar_os(){
        String sql = "SELECT max(os) from tb_os";
        try {
            pst = conexao.prepareStatement(sql);
            rs = pst.executeQuery();
            
            if(rs.next() == true){
                lblOs.setText(rs.getString(1));
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }
    
    //Limpar campos e gerenciar botões.
    private void limparCampos(){
        //LimparCampos
        txtEquipamento.setText(null);
        txtDefeito.setText(null);
        txtServico.setText(null);
        txtTecnico.setText(null);
        txtValor.setText(null);
        lblCliId.setText(null);
        lblOs.setText(null);
        lblData.setText(null);
        txtCliPesquisar.setText(null);
        ((DefaultTableModel) tblClientes.getModel()).setRowCount(0);
        cbSituacao.setSelectedItem(" ");
        
        //habilitar os botões
        btnOsAdd.setEnabled(true);
        btnOsPesquisar.setEnabled(true);
        txtCliPesquisar.setEditable(true);
        tblClientes.setVisible(true);
        
        //Desabilitar os botões
        btnOsAlterar.setEnabled(false);
        btnOsExcluir.setEnabled(false);
        btnOsImprimir.setEnabled(false);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        rbtOrc = new javax.swing.JRadioButton();
        rbtOs = new javax.swing.JRadioButton();
        lblOs = new javax.swing.JLabel();
        lblData = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        cbSituacao = new javax.swing.JComboBox<>();
        jPanel2 = new javax.swing.JPanel();
        txtCliPesquisar = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblClientes = new javax.swing.JTable();
        lblCliId = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        txtEquipamento = new javax.swing.JTextField();
        txtDefeito = new javax.swing.JTextField();
        txtServico = new javax.swing.JTextField();
        txtTecnico = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        txtValor = new javax.swing.JTextField();
        btnOsAdd = new javax.swing.JButton();
        btnOsPesquisar = new javax.swing.JButton();
        btnOsAlterar = new javax.swing.JButton();
        btnOsExcluir = new javax.swing.JButton();
        btnOsImprimir = new javax.swing.JButton();

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setTitle("OS");
        setPreferredSize(new java.awt.Dimension(673, 690));
        addInternalFrameListener(new javax.swing.event.InternalFrameListener() {
            public void internalFrameOpened(javax.swing.event.InternalFrameEvent evt) {
                formInternalFrameOpened(evt);
            }
            public void internalFrameClosing(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameClosed(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameIconified(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameDeiconified(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameActivated(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameDeactivated(javax.swing.event.InternalFrameEvent evt) {
            }
        });

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel1.setText("N OS");

        jLabel2.setText("Data");

        buttonGroup1.add(rbtOrc);
        rbtOrc.setText("Orçamento");
        rbtOrc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbtOrcActionPerformed(evt);
            }
        });

        buttonGroup1.add(rbtOs);
        rbtOs.setText("Ordem de Serviço");
        rbtOs.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbtOsActionPerformed(evt);
            }
        });

        lblOs.setText("****");

        lblData.setText("****");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(lblOs))
                        .addGap(40, 40, 40)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2)
                            .addComponent(lblData)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(rbtOrc)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(rbtOs)))
                .addContainerGap(21, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblOs)
                    .addComponent(lblData))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(rbtOrc)
                    .addComponent(rbtOs))
                .addContainerGap(21, Short.MAX_VALUE))
        );

        jLabel3.setText("*Situação");

        cbSituacao.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { " ", "Na bancada", "Entrega OK", "Orçamento REPROVADO", "Aguardando Aprovação", "Aguardando peças", "Abandonado pelo cliente", "Retornou" }));

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Cliente"));

        txtCliPesquisar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtCliPesquisarKeyReleased(evt);
            }
        });

        jLabel4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/projeto_sc/icones/search_icon_32.png"))); // NOI18N

        jLabel5.setText("*ID");

        tblClientes.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "ID", "Nome", "Telefone"
            }
        ));
        tblClientes.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblClientesMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblClientes);

        lblCliId.setText("****");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(txtCliPesquisar, javax.swing.GroupLayout.PREFERRED_SIZE, 182, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblCliId)
                .addContainerGap(34, Short.MAX_VALUE))
            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(txtCliPesquisar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel5)
                            .addComponent(lblCliId)))
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 59, Short.MAX_VALUE)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jLabel6.setText("*Equipamento");

        jLabel7.setText("*Defeito");

        jLabel8.setText("Serviço");

        jLabel9.setText("Tecnico");

        jLabel10.setText("Valor Total");

        txtValor.setText("0");

        btnOsAdd.setIcon(new javax.swing.ImageIcon(getClass().getResource("/projeto_sc/icones/icon_create64.png"))); // NOI18N
        btnOsAdd.setToolTipText("Adicionar OS");
        btnOsAdd.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        btnOsAdd.setPreferredSize(new java.awt.Dimension(80, 80));
        btnOsAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOsAddActionPerformed(evt);
            }
        });

        btnOsPesquisar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/projeto_sc/icones/icon_read64.png"))); // NOI18N
        btnOsPesquisar.setToolTipText("Consultar OS");
        btnOsPesquisar.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        btnOsPesquisar.setPreferredSize(new java.awt.Dimension(80, 80));
        btnOsPesquisar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOsPesquisarActionPerformed(evt);
            }
        });

        btnOsAlterar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/projeto_sc/icones/icon_update64.png"))); // NOI18N
        btnOsAlterar.setToolTipText("Alterar OS");
        btnOsAlterar.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        btnOsAlterar.setEnabled(false);
        btnOsAlterar.setPreferredSize(new java.awt.Dimension(80, 80));
        btnOsAlterar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOsAlterarActionPerformed(evt);
            }
        });

        btnOsExcluir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/projeto_sc/icones/icon_delete64.png"))); // NOI18N
        btnOsExcluir.setToolTipText("Deletar OS");
        btnOsExcluir.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        btnOsExcluir.setEnabled(false);
        btnOsExcluir.setPreferredSize(new java.awt.Dimension(80, 80));
        btnOsExcluir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOsExcluirActionPerformed(evt);
            }
        });

        btnOsImprimir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/projeto_sc/icones/impressora.png"))); // NOI18N
        btnOsImprimir.setToolTipText("Imprimir OS");
        btnOsImprimir.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        btnOsImprimir.setEnabled(false);
        btnOsImprimir.setPreferredSize(new java.awt.Dimension(80, 80));
        btnOsImprimir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOsImprimirActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(15, 15, 15)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel3)
                                        .addGap(18, 18, 18)
                                        .addComponent(cbSituacao, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 26, Short.MAX_VALUE)
                                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel9)
                                    .addComponent(jLabel8)
                                    .addComponent(jLabel7)
                                    .addComponent(jLabel6))
                                .addGap(48, 48, 48)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtEquipamento)
                                    .addComponent(txtServico)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(txtTecnico, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(18, 18, 18)
                                        .addComponent(jLabel10)
                                        .addGap(18, 18, 18)
                                        .addComponent(txtValor))
                                    .addComponent(txtDefeito)))))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(57, 57, 57)
                        .addComponent(btnOsAdd, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(31, 31, 31)
                        .addComponent(btnOsPesquisar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(39, 39, 39)
                        .addComponent(btnOsAlterar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(37, 37, 37)
                        .addComponent(btnOsExcluir, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(35, 35, 35)
                        .addComponent(btnOsImprimir, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(46, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3)
                            .addComponent(cbSituacao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(88, 88, 88)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(txtEquipamento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(txtDefeito, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(txtServico, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(txtTecnico, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10)
                    .addComponent(txtValor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 80, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnOsPesquisar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnOsAlterar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnOsExcluir, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnOsAdd, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnOsImprimir, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(32, 32, 32))
        );

        setBounds(0, 0, 673, 690);
    }// </editor-fold>//GEN-END:initComponents

    private void rbtOrcActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbtOrcActionPerformed
        // TODO add your handling code here:
        tipo = "Orçamento";
    }//GEN-LAST:event_rbtOrcActionPerformed

    private void txtCliPesquisarKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtCliPesquisarKeyReleased
        // TODO add your handling code here:
        pesquisarCliente();
    }//GEN-LAST:event_txtCliPesquisarKeyReleased

    private void tblClientesMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblClientesMouseClicked
        // TODO add your handling code here:
        setarCampos();
    }//GEN-LAST:event_tblClientesMouseClicked

    private void rbtOsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbtOsActionPerformed
        // TODO add your handling code here:
        tipo = "OS";
    }//GEN-LAST:event_rbtOsActionPerformed

    private void formInternalFrameOpened(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameOpened
        // TODO add your handling code here:
        rbtOrc.setSelected(true);
        tipo = "Orçamento";
    }//GEN-LAST:event_formInternalFrameOpened

    private void btnOsAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOsAddActionPerformed
        // TODO add your handling code here:
        emitir_os();
    }//GEN-LAST:event_btnOsAddActionPerformed

    private void btnOsPesquisarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOsPesquisarActionPerformed
        // TODO add your handling code here:
        pesquisar_os();
    }//GEN-LAST:event_btnOsPesquisarActionPerformed

    private void btnOsAlterarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOsAlterarActionPerformed
        // TODO add your handling code here:
        alterar_os();
    }//GEN-LAST:event_btnOsAlterarActionPerformed

    private void btnOsExcluirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOsExcluirActionPerformed
        // TODO add your handling code here:
        excluir_os();
    }//GEN-LAST:event_btnOsExcluirActionPerformed

    private void btnOsImprimirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOsImprimirActionPerformed
        // TODO add your handling code here:
        //Chamando método para imprimir uma OS
        imprimir_os();
    }//GEN-LAST:event_btnOsImprimirActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnOsAdd;
    private javax.swing.JButton btnOsAlterar;
    private javax.swing.JButton btnOsExcluir;
    private javax.swing.JButton btnOsImprimir;
    private javax.swing.JButton btnOsPesquisar;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JComboBox<String> cbSituacao;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblCliId;
    private javax.swing.JLabel lblData;
    private javax.swing.JLabel lblOs;
    private javax.swing.JRadioButton rbtOrc;
    private javax.swing.JRadioButton rbtOs;
    private javax.swing.JTable tblClientes;
    private javax.swing.JTextField txtCliPesquisar;
    private javax.swing.JTextField txtDefeito;
    private javax.swing.JTextField txtEquipamento;
    private javax.swing.JTextField txtServico;
    private javax.swing.JTextField txtTecnico;
    private javax.swing.JTextField txtValor;
    // End of variables declaration//GEN-END:variables
}
