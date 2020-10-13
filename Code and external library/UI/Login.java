/*
 * Description:The login interface, is completed with the help of JFormdesigner plugins.
 *
 */

package UI;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import PostgreSQLJDBC.Database;
import ProductAndUser.SalaryStructure;


/**
 * @author unknown
 */
public class Login {
    private String userID; //The number of login account
    private String password;// The password of login account
    private String userType;// The account type of login account


    public Login() {
        initComponents();
    }

/**
 *  Login button
 * @param e :  button click
 * @return void
 */
    private void loginButton(MouseEvent e) {
        userID = textField1.getText();
        password = new String(passwordField1.getPassword());
        Utility utility = new Utility();
        Database database = new Database();
        database.connect();
        // Verify that the database is initialized, if not, initialize
        if (database.ifInitialize() == 0) {
            database.initialize();
        }
        //Check whether the account password is correct.
        //There are different constructors according to different account types
        userType = database.authorization(userID);
        if (database.accountCheck(userID, utility.shaEncrypt(password))) {
            loginInterface.setVisible(false);
            if (userType.equals("Admin")) {
                MainInterface mainInterface = new MainInterface(userID, database.authorization(userID), database);
                mainInterface.setVisible(true);
            } else {
                SalaryStructure salaryStructure = new SalaryStructure();
                database.getSalaryStructure(salaryStructure, userType);
                MainInterface mainInterface = new MainInterface(userID, database.authorization(userID), database, salaryStructure);
                mainInterface.setVisible(true);
            }
        } else {
            wrongWarn.setVisible(true);
        }
    }


    private void bTryAgain(MouseEvent e) {
        wrongWarn.setVisible(false);
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner Evaluation license - unknown
        loginInterface = new JFrame();
        panel1 = new JPanel();
        lWelcome = new JLabel();
        lUserID = new JLabel();
        lPassword = new JLabel();
        textField1 = new JTextField();
        passwordField1 = new JPasswordField();
        bLogin = new JButton();
        lBackPicture = new JLabel();
        wrongWarn = new JDialog();
        lWrong = new JLabel();
        bBack = new JButton();

        //======== loginInterface ========
        {
            loginInterface.setTitle("Account System");
            loginInterface.setVisible(true);
            var loginInterfaceContentPane = loginInterface.getContentPane();
            loginInterfaceContentPane.setLayout(null);

            //======== panel1 ========
            {
                panel1.setBorder (new javax. swing. border. CompoundBorder( new javax .swing .border .TitledBorder (
                new javax. swing. border. EmptyBorder( 0, 0, 0, 0) , "JF\u006frmDesi\u0067ner Ev\u0061luatio\u006e"
                , javax. swing. border. TitledBorder. CENTER, javax. swing. border. TitledBorder. BOTTOM
                , new java .awt .Font ("Dialo\u0067" ,java .awt .Font .BOLD ,12 )
                , java. awt. Color. red) ,panel1. getBorder( )) ); panel1. addPropertyChangeListener (
                new java. beans. PropertyChangeListener( ){ @Override public void propertyChange (java .beans .PropertyChangeEvent e
                ) {if ("borde\u0072" .equals (e .getPropertyName () )) throw new RuntimeException( )
                ; }} );
                panel1.setLayout(null);

                //---- lWelcome ----
                lWelcome.setText("Welcome! Please login");
                lWelcome.setFont(new Font("Microsoft YaHei UI", Font.BOLD | Font.ITALIC, 24));
                panel1.add(lWelcome);
                lWelcome.setBounds(80, 50, 360, 85);

                //---- lUserID ----
                lUserID.setText("UserID:");
                lUserID.setFont(new Font("Microsoft YaHei UI", Font.BOLD, 16));
                panel1.add(lUserID);
                lUserID.setBounds(140, 165, 100, 75);

                //---- lPassword ----
                lPassword.setText("Password:");
                lPassword.setFont(new Font("Microsoft YaHei UI", Font.BOLD, 16));
                panel1.add(lPassword);
                lPassword.setBounds(140, 235, 100, 45);
                panel1.add(textField1);
                textField1.setBounds(275, 180, 220, 40);
                panel1.add(passwordField1);
                passwordField1.setBounds(275, 235, 220, 40);

                //---- bLogin ----
                bLogin.setText("Login");
                bLogin.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        loginButton(e);
                    }
                });
                panel1.add(bLogin);
                bLogin.setBounds(395, 300, 105, 35);

                //---- lBackPicture ----
                lBackPicture.setIcon(new ImageIcon("C:\\Users\\AM\\Desktop\\\u6bd5\u4e1a\u8bbe\u8ba1\\picture\\part-00377-3243.jpg"));
                panel1.add(lBackPicture);
                lBackPicture.setBounds(0, 0, 655, 405);

                {
                    // compute preferred size
                    Dimension preferredSize = new Dimension();
                    for(int i = 0; i < panel1.getComponentCount(); i++) {
                        Rectangle bounds = panel1.getComponent(i).getBounds();
                        preferredSize.width = Math.max(bounds.x + bounds.width, preferredSize.width);
                        preferredSize.height = Math.max(bounds.y + bounds.height, preferredSize.height);
                    }
                    Insets insets = panel1.getInsets();
                    preferredSize.width += insets.right;
                    preferredSize.height += insets.bottom;
                    panel1.setMinimumSize(preferredSize);
                    panel1.setPreferredSize(preferredSize);
                }
            }
            loginInterfaceContentPane.add(panel1);
            panel1.setBounds(0, -5, 655, 405);

            {
                // compute preferred size
                Dimension preferredSize = new Dimension();
                for(int i = 0; i < loginInterfaceContentPane.getComponentCount(); i++) {
                    Rectangle bounds = loginInterfaceContentPane.getComponent(i).getBounds();
                    preferredSize.width = Math.max(bounds.x + bounds.width, preferredSize.width);
                    preferredSize.height = Math.max(bounds.y + bounds.height, preferredSize.height);
                }
                Insets insets = loginInterfaceContentPane.getInsets();
                preferredSize.width += insets.right;
                preferredSize.height += insets.bottom;
                loginInterfaceContentPane.setMinimumSize(preferredSize);
                loginInterfaceContentPane.setPreferredSize(preferredSize);
            }
            loginInterface.setSize(655, 430);
            loginInterface.setLocationRelativeTo(null);
        }

        //======== wrongWarn ========
        {
            wrongWarn.setTitle("Invalid login, please try again");
            var wrongWarnContentPane = wrongWarn.getContentPane();
            wrongWarnContentPane.setLayout(null);

            //---- lWrong ----
            lWrong.setText("Wrong UserID or Password");
            lWrong.setFont(lWrong.getFont().deriveFont(lWrong.getFont().getSize() + 4f));
            wrongWarnContentPane.add(lWrong);
            lWrong.setBounds(60, 35, 225, 50);

            //---- bBack ----
            bBack.setText("Try again");
            bBack.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    bTryAgain(e);
                }
            });
            wrongWarnContentPane.add(bBack);
            bBack.setBounds(180, 90, 120, 35);

            {
                // compute preferred size
                Dimension preferredSize = new Dimension();
                for(int i = 0; i < wrongWarnContentPane.getComponentCount(); i++) {
                    Rectangle bounds = wrongWarnContentPane.getComponent(i).getBounds();
                    preferredSize.width = Math.max(bounds.x + bounds.width, preferredSize.width);
                    preferredSize.height = Math.max(bounds.y + bounds.height, preferredSize.height);
                }
                Insets insets = wrongWarnContentPane.getInsets();
                preferredSize.width += insets.right;
                preferredSize.height += insets.bottom;
                wrongWarnContentPane.setMinimumSize(preferredSize);
                wrongWarnContentPane.setPreferredSize(preferredSize);
            }
            wrongWarn.setSize(355, 185);
            wrongWarn.setLocationRelativeTo(null);
        }
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner Evaluation license - unknown
    private JFrame loginInterface;
    private JPanel panel1;
    private JLabel lWelcome;
    private JLabel lUserID;
    private JLabel lPassword;
    private JTextField textField1;
    private JPasswordField passwordField1;
    private JButton bLogin;
    private JLabel lBackPicture;
    private JDialog wrongWarn;
    private JLabel lWrong;
    private JButton bBack;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
