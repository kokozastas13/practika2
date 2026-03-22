import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

// ==========================================================
// 1. ІНТЕРФЕЙС "ФАБРИКОВАНИХ" ОБ'ЄКТІВ (Завдання 3, 4)
// ==========================================================
interface ResultInterface extends Serializable {
    void display(JTextArea area); // Метод для текстового відображення
    int getTransitions();         // Для логіки обчислень
}

// Конкретний клас результату
class BitResult implements ResultInterface {
    private static final long serialVersionUID = 1L;
    private int number;
    private int transitions;
    private String binary;

    public BitResult(int number, int transitions, String binary) {
        this.number = number;
        this.transitions = transitions;
        this.binary = binary;
    }

    @Override
    public void display(JTextArea area) {
        area.append(String.format("Число: %d | Бінарно: %s | Чергувань: %d\n", 
                    number, binary, transitions));
    }

    @Override
    public int getTransitions() { return transitions; }
}

// ==========================================================
// 2. ІНТЕРФЕЙС ТА КЛАС ФАБРИКИ (Завдання 2, 5)
// ==========================================================
interface ResultFactory {
    ResultInterface createResult(int number);
}

class BitAnalysisFactory implements ResultFactory {
    @Override
    public ResultInterface createResult(int number) {
        String bin = Integer.toBinaryString(number);
        int count = 0;
        for (int i = 0; i < bin.length() - 1; i++) {
            if (bin.charAt(i) != bin.charAt(i + 1)) count++;
        }
        return new BitResult(number, count, bin);
    }
}

// ==========================================================
// 3. ГОЛОВНЕ ВІКНО З КОЛЕКЦІЄЮ ТА GUI
// ==========================================================
public class Lab3FactorySolution extends JFrame {
    private JTextField inputField;
    private JTextArea logArea;
    
    // Колекція для зберігання результатів (Завдання 1)
    private List<ResultInterface> resultsStore = new ArrayList<>();
    private ResultFactory factory = new BitAnalysisFactory();
    private static final String FILE_NAME = "collection_data.ser";

    public Lab3FactorySolution() {
        setTitle("Lab 3: Factory Method & Collections");
        setSize(600, 450);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Панель керування
        JPanel controlPanel = new JPanel();
        inputField = new JTextField(10);
        JButton btnAdd = new JButton("Додати в колекцію");
        JButton btnSave = new JButton("Зберегти все");
        JButton btnLoad = new JButton("Відновити");
        JButton btnClear = new JButton("Очистити");

        controlPanel.add(new JLabel("Число:"));
        controlPanel.add(inputField);
        controlPanel.add(btnAdd);
        
        JPanel filePanel = new JPanel();
        filePanel.add(btnSave);
        filePanel.add(btnLoad);
        filePanel.add(btnClear);

        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setFont(new Font("Consolas", Font.PLAIN, 12));

        add(controlPanel, BorderLayout.NORTH);
        add(new JScrollPane(logArea), BorderLayout.CENTER);
        add(filePanel, BorderLayout.SOUTH);

        // Події
        btnAdd.addActionListener(e -> addNewResult());
        btnSave.addActionListener(e -> saveCollection());
        btnLoad.addActionListener(e -> loadCollection());
        btnClear.addActionListener(e -> {
            resultsStore.clear();
            refreshLog();
        });
    }

    private void addNewResult() {
        try {
            int num = Integer.parseInt(inputField.getText());
            // Використання Factory Method
            ResultInterface newRes = factory.createResult(num);
            resultsStore.add(newRes);
            refreshLog();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Введіть число!");
        }
    }

    private void refreshLog() {
        logArea.setText("--- Поточна колекція результатів ---\n");
        for (ResultInterface res : resultsStore) {
            res.display(logArea);
        }
        logArea.append("Всього елементів: " + resultsStore.size() + "\n");
    }

    // Збереження всієї колекції (Завдання 1)
    private void saveCollection() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            oos.writeObject(resultsStore);
            logArea.append(">>> Колекцію збережено успішно!\n");
        } catch (IOException ex) {
            logArea.append("Помилка запису: " + ex.getMessage() + "\n");
        }
    }

    // Відновлення колекції (Завдання 1)
    @SuppressWarnings("unchecked")
    private void loadCollection() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_NAME))) {
            resultsStore = (List<ResultInterface>) ois.readObject();
            refreshLog();
            logArea.append(">>> Колекцію відновлено з файлу.\n");
        } catch (Exception ex) {
            logArea.append("Помилка завантаження: " + ex.getMessage() + "\n");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Lab3FactorySolution().setVisible(true));
    }
}