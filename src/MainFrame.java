import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class MainFrame extends JFrame {

    private static final String FRAME_TITLE = "Клиент мгновенных сообщений";
    
    private static final int FRAME_MINIMUM_WIDTH = 500;
    private static final int FRAME_MINIMUM_HEIGHT = 500;

    private static final int FROM_FIELD_DEFAULT_COLUMNS = 10;
    private static final int TO_FIELD_DEFAULT_COLUMNS = 20;
    private static final int INCOMING_AREA_DEFAULT_ROWS = 10;
    private static final int OUTGOING_AREA_DEFAULT_ROWS = 5;

    private static final int SMALL_GAP = 5;
    private static final int MEDIUM_GAP = 10;
    private static final int LARGE_GAP = 15;
    private static final int SERVER_PORT = 4567;

    private final JTextField textFieldFrom;
    private final JTextField textFieldTo;

    private final JTextArea textAreaIncoming;
    private final JTextArea textAreaOutgoing;
    private String date;

    public MainFrame() {
        super(FRAME_TITLE); // Вызывает конструктор родительского класса JFrame и устанавливает заголовок окна.

        setMinimumSize(new Dimension(FRAME_MINIMUM_WIDTH, FRAME_MINIMUM_HEIGHT)); // Устанавливает минимальный размер окна.

        final Toolkit kit = Toolkit.getDefaultToolkit(); // Получает набор инструментов для интерфейса пользователя.

        setLocation((kit.getScreenSize().width - getWidth()) / 2, // Располагает окно по центру экрана.
                (kit.getScreenSize().height - getHeight()) / 2);

        textAreaIncoming = new JTextArea(INCOMING_AREA_DEFAULT_ROWS, 0); // Создает текстовую область для входящих сообщений.
        textAreaIncoming.setEditable(false); // Запрещает редактирование текстовой области.

        final JScrollPane scrollPaneIncoming = new JScrollPane(textAreaIncoming); // Добавляет прокрутку к текстовой области.

        final JLabel labelFrom = new JLabel("От"); // Создает метку "От".
        final JLabel labelTo = new JLabel("Получатель"); // Создает метку "Получатель".

        textFieldFrom = new JTextField(FROM_FIELD_DEFAULT_COLUMNS); // Создает текстовое поле для отправителя.
        textFieldTo = new JTextField(TO_FIELD_DEFAULT_COLUMNS); // Создает текстовое поле для получателя.
        textAreaOutgoing = new JTextArea(OUTGOING_AREA_DEFAULT_ROWS, 0); // Создает текстовую область для исходящих сообщений.

        final JScrollPane scrollPaneOutgoing = new JScrollPane(textAreaOutgoing); // Добавляет прокрутку к текстовой области исходящих сообщений.

        final JPanel messagePanel = new JPanel(); // Создает панель для размещения элементов управления сообщениями.
        messagePanel.setBorder(BorderFactory.createTitledBorder("Сообщение")); // Устанавливает рамку с заголовком "Сообщение".

        final JButton sendButton = new JButton("Отправить"); // Создает кнопку "Отправить".

        sendButton.addActionListener(e -> { // Добавляет слушателя событий для кнопки.
            date = getDateTime(); // Получает текущую дату и время.
            sendMessage(); // Вызывает метод отправки сообщения.
        });

        final GroupLayout layout2 = new GroupLayout(messagePanel); // Создает менеджер компоновки для панели сообщений.
        messagePanel.setLayout(layout2); // Устанавливает менеджер компоновки для панели.
        layout2.setHorizontalGroup(layout2.createSequentialGroup() // Определяет горизонтальную группу компоновки.
                .addContainerGap()
                .addGroup(layout2.createParallelGroup(Alignment.TRAILING)
                        .addGroup(layout2
                                .createSequentialGroup()
                                .addComponent(labelFrom)
                                .addGap(SMALL_GAP)
                                .addComponent(textFieldFrom)
                                .addGap(LARGE_GAP)
                                .addComponent(labelTo)
                                .addGap(SMALL_GAP)
                                .addComponent(textFieldTo))
                        .addComponent(scrollPaneOutgoing)
                        .addComponent(sendButton))
                .addContainerGap());

        layout2.setVerticalGroup(layout2.createSequentialGroup() // Определяет вертикальную группу компоновки.
                .addContainerGap()
                .addGroup(layout2
                        .createParallelGroup(Alignment.BASELINE)
                        .addComponent(labelFrom)
                        .addComponent(textFieldFrom).addComponent(labelTo).addComponent(textFieldTo))
                .addGap(MEDIUM_GAP)
                .addComponent(scrollPaneOutgoing)
                .addGap(MEDIUM_GAP)
                .addComponent(sendButton)
                .addContainerGap());

        final GroupLayout layout1 = new GroupLayout(getContentPane()); // Создает менеджер компоновки для основного контейнера.
        setLayout(layout1); // Устанавливает менеджер компоновки для основного контейнера.
        layout1.setHorizontalGroup(layout1.createSequentialGroup() // Определяет горизонтальную группу компоновки для основного контейнера.
                .addContainerGap()
                .addGroup(layout1.createParallelGroup()
                        .addComponent(scrollPaneIncoming)
                        .addComponent(messagePanel))
                .addContainerGap());

        layout1.setVerticalGroup(layout1.createSequentialGroup() // Определяет вертикальную группу компоновки для основного контейнера.
                .addContainerGap()
                .addComponent(scrollPaneIncoming)
                .addGap(MEDIUM_GAP)
                .addComponent(messagePanel)
                .addContainerGap());

        new Thread(() -> { // Создание нового потока с использованием лямбда-выражения.
            try {
                final ServerSocket serverSocket = new ServerSocket(SERVER_PORT); // Создание серверного сокета на определенном порту.
                while (!Thread.interrupted()) { // Начало бесконечного цикла, который продолжается, пока поток не будет прерван.
                    final Socket socket = serverSocket.accept(); // Ожидание и принятие входящего соединения.
                    final DataInputStream in = new DataInputStream(socket.getInputStream()); // Создание потока ввода данных из сокета.
                    final String senderName = in.readUTF(); // Чтение имени отправителя из потока.
                    final String message = in.readUTF(); // Чтение сообщения из потока.
                    final String Dat = in.readUTF(); // Чтение даты из потока.
                    socket.close(); // Закрытие сокета.

                    final String address =((InetSocketAddress) socket
                            .getRemoteSocketAddress()).getAddress().getHostAddress(); // Получение IP-адреса отправителя.
                    textAreaIncoming.append(Dat+"  "+senderName + " (" + address + "): " + message + "\n"); // Добавление информации в текстовую область входящих сообщений.
                }

            } catch (IOException e) { // Обработка исключений ввода-вывода.
                e.printStackTrace(); // Печать трассировки стека исключения.
                JOptionPane.showMessageDialog(MainFrame.this, "Ошибка в работе сервера", // Вывод сообщения об ошибке.
                        "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        }).start(); // Запуск потока
    }



    @SuppressWarnings("CallToPrintStackTrace")
    private void sendMessage() { // Определение метода для отправки сообщений.
        try {
            final String senderName = textFieldFrom.getText(); // Получение имени отправителя из текстового поля.
            final String destinationAddress = textFieldTo.getText(); // Получение адреса получателя из текстового поля.
            final String message = textAreaOutgoing.getText(); // Получение текста сообщения из текстовой области.
            final String _Dat = date; // Получение текущей даты и времени.

            // Проверка на пустоту адреса получателя.
            if(!destinationAddress.isEmpty()){
                // Регулярное выражение для проверки IP-адреса.
                Pattern p = Pattern.compile("[0-9][0-9]?[0-9]?.[0-9][0-9]?[0-9]?.[0-9][0-9]?[0-9]?.[0-9][0-9]?[0-9]?");
                Matcher m = p.matcher(destinationAddress); // Создание объекта для сопоставления с шаблоном.
                boolean b = m.matches(); // Проверка соответствия адреса шаблону.
                // Если адрес не соответствует шаблону IP-адреса, выводится сообщение об ошибке.

                if(!b) {
                    JOptionPane.showMessageDialog(this, "Некорректно введен IP" , "Ошибка",
                            JOptionPane.ERROR_MESSAGE);
                    textFieldTo.grabFocus(); // Фокусировка на текстовом поле адреса.
                    return; // Прерывание выполнения метода.
                }
            }

            // Проверки на пустоту полей и вывод соответствующих сообщений об ошибках.
            if (senderName.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Введите имя отправителя", "Ошибка",
                        JOptionPane.ERROR_MESSAGE);
                textFieldFrom.grabFocus();
                return;
            }

            if (destinationAddress.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Введите адрес узла-получателя", "Ошибка",
                        JOptionPane.ERROR_MESSAGE);
                textFieldTo.grabFocus();
                return;
            }

            if (message.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Введите текст сообщения", "Ошибка",
                        JOptionPane.ERROR_MESSAGE);
                textAreaOutgoing.grabFocus();
                return;
            }

            // Создание клиентского сокета для отправки сообщения.
            final Socket socket = new Socket(destinationAddress, SERVER_PORT);

            final DataOutputStream out = new DataOutputStream(socket.getOutputStream()); // Создание потока вывода данных в сокет.

            // Отправка информации через сокет.
            out.writeUTF(senderName);
            out.writeUTF(message);
            out.writeUTF(_Dat);
            socket.close(); // Закрытие сокета.

            // Добавление информации о отправленном сообщении в текстовую область входящих сообщений.
            textAreaIncoming.append(_Dat+"  Я -> " + destinationAddress + ": " + message + "\n");
            textAreaOutgoing.setText(""); // Очистка текстовой области исходящих сообщений.

        }
        // Обработка исключений, связанных с неизвестным хостом и вводом-выводом.
        catch (UnknownHostException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(MainFrame.this,
                    "Не удалось отправить сообщение: узел-адресат не найден",
                    "Ошибка", JOptionPane.ERROR_MESSAGE);
        }

        catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(MainFrame.this, "Не удалось отправить сообщение",
                    "Ошибка",JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            final MainFrame frame = new MainFrame();
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setVisible(true);
        });
    }

    private String getDateTime() {

        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

        Date date = new Date();

        return dateFormat.format(date);

    }
}

