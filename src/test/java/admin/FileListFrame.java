package admin;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author quavario@gmail.com
 * @date 2021/9/24 16:34
 */
public class FileListFrame extends JFrame {
    private static final long serialVersionUID = -3735840332208017268L;

    private JPanel contentPane;

    private JTextField chooseTextField;

    private JTable table;

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    FileListFrame frame = new FileListFrame();

                    frame.setVisible(true);

                } catch (Exception e) {
                    e.printStackTrace();

                }

            }

        });

    }

    /**

     * Create the frame.

     */

    public FileListFrame() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setBounds(100, 100, 450, 300);

        contentPane = new JPanel();

        contentPane.setLayout(new BorderLayout(0, 0));

        setContentPane(contentPane);

        JPanel panel = new JPanel();

        contentPane.add(panel, BorderLayout.NORTH);

        JLabel chooseLabel = new JLabel("\u9009\u62E9\u6587\u4EF6\u5939\uFF1A");

        panel.add(chooseLabel);

        chooseTextField = new JTextField();

        panel.add(chooseTextField);

        chooseTextField.setColumns(15);

        JButton chooseButton = new JButton("\u9009\u62E9");

        chooseButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                do_chooseButton_actionPerformed(e);

            }

        });

        panel.add(chooseButton);

        JScrollPane scrollPane = new JScrollPane();

        contentPane.add(scrollPane, BorderLayout.CENTER);

        table = new JTable();

        DefaultTableModel model = (DefaultTableModel) table.getModel();

        model.setColumnIdentifiers(new String[] { "?????????", "????????????", "????????????", });

        scrollPane.setViewportView(table);

    }

    protected void do_chooseButton_actionPerformed(ActionEvent e) {
        JFileChooser chooser = new JFileChooser();// ?????????????????????

        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);// ???????????????????????????

        chooser.setMultiSelectionEnabled(false);// ??????????????????

        int result = chooser.showOpenDialog(this);// ?????????????????????

        if (result == JFileChooser.APPROVE_OPTION) {
            File selectDirectory = chooser.getSelectedFile();// ????????????????????????

            chooseTextField.setText(selectDirectory.getAbsolutePath());// ?????????????????????

            final File[] files = selectDirectory.listFiles();// ????????????????????????????????????

            final DefaultTableModel model = (DefaultTableModel) table.getModel();// ??????????????????

            final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// ???????????????????????????

            new Thread() {
                public void run() {
                    for (File file : files) {
                        if (file.isFile()) {// ????????????????????????????????????????????????

                            model.addRow(new Object[] { file.getName(), file.length(), format.format(new Date(file.lastModified())) });

                            try {
                                Thread.sleep(1000);// ??????????????????1????????????????????????

                            } catch (InterruptedException e) {
                                e.printStackTrace();

                            }

                        }

                    }

                };

            }.start();

        }

    }

}
