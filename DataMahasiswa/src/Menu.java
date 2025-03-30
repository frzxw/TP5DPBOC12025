import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableModel;
import javax.xml.crypto.Data;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class Menu extends JFrame {
    public static void main(String[] args) {
        // buat object window
        Menu window = new Menu();

        // atur ukuran window
        window.setSize(480, 560);

        // letakkan window di tengah layar
        window.setLocationRelativeTo(null);

        // isi window
        window.setContentPane(window.mainPanel);

        // ubah warna background
        window.getContentPane().setBackground(Color.white);

        // tampilkan window
        window.setVisible(true);

        // agar program ikut berhenti saat window diclose
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    // index baris yang diklik
    private int selectedIndex = -1;
    // list untuk menampung semua mahasiswa
    private ArrayList<Mahasiswa> listMahasiswa;

    private Database database;

    private JPanel mainPanel;
    private JTextField nimField;
    private JTextField namaField;
    private JTable mahasiswaTable;
    private JButton addUpdateButton;
    private JButton cancelButton;
    private JComboBox<String> jenisKelaminComboBox;
    private JButton deleteButton;
    private JLabel titleLabel;
    private JLabel nimLabel;
    private JLabel namaLabel;
    private JLabel jenisKelaminLabel;
    private JLabel usiaLabel;
    private JLabel kelasLabel;
    private JSlider usiaSlider;
    private JRadioButton kelasC1Button;
    private JRadioButton kelasC2Button;
    private final ButtonGroup kelasButtonGroup;

    // constructor
    public Menu() {
        // inisialisasi listMahasiswa
        listMahasiswa = new ArrayList<>();

        // buat objek database
        database = new Database();

        // isi tabel mahasiswa
        mahasiswaTable.setModel(setTable());

        // ubah styling title
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 20f));

        // atur isi combo box
        String[] jenisKelaminData = { "Laki-laki", "Perempuan" };
        jenisKelaminComboBox.setModel(new DefaultComboBoxModel<String>(jenisKelaminData));

        // inisialisasi button group untuk radio button kelas
        kelasButtonGroup = new ButtonGroup();
        kelasButtonGroup.add(kelasC1Button);
        kelasButtonGroup.add(kelasC2Button);

        // inisialisasi range untuk usiaSlider
        usiaSlider.setMinimum(17); // Nilai minimum usia
        usiaSlider.setMaximum(25); // Nilai maksimum usia
        usiaSlider.setValue(18); // Nilai default
        usiaSlider.setMajorTickSpacing(1); // Tick utama setiap 1 tahun
        usiaSlider.setPaintTicks(true); // Tampilkan tick marks
        usiaSlider.setPaintLabels(true); // Tampilkan label pada tick

        // sembunyikan button delete
        deleteButton.setVisible(false);

        // saat tombol add/update ditekan
        addUpdateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (selectedIndex == -1) {
                    insertData();
                } else {
                    updateData();
                }
            }
        });
        // saat tombol delete ditekan
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (selectedIndex != -1) {
                    deleteData();
                }
            }
        });
        // saat tombol cancel ditekan
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearForm();
            }
        });
        // saat salah satu baris tabel ditekan
        mahasiswaTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                // ubah selectedIndex menjadi baris tabel yang diklik
                selectedIndex = mahasiswaTable.getSelectedRow();

                // simpan value textfield, combo box, slider, dan radio button
                String selectedNim = mahasiswaTable.getValueAt(selectedIndex, 1).toString();
                String selectedNama = mahasiswaTable.getValueAt(selectedIndex, 2).toString();
                String selectedJenisKelamin = mahasiswaTable.getValueAt(selectedIndex, 3).toString();
                int selectedUsia = Integer.parseInt(mahasiswaTable.getValueAt(selectedIndex, 4).toString());
                String selectedKelas = mahasiswaTable.getValueAt(selectedIndex, 5).toString();

                // ubah isi textfield, combo box, slider, dan radio button
                nimField.setText(selectedNim);
                namaField.setText(selectedNama);
                jenisKelaminComboBox.setSelectedItem(selectedJenisKelamin);
                usiaSlider.setValue(selectedUsia);
                // usiaValueLabel akan otomatis diupdate oleh ChangeListener
                if (selectedKelas.equals("C1")) {
                    kelasC1Button.setSelected(true);
                } else {
                    kelasC2Button.setSelected(true);
                }

                // ubah button "Add" menjadi "Update"
                addUpdateButton.setText("Update");

                // tampilkan button delete
                deleteButton.setVisible(true);
            }
        });
    }

    public void insertData() {
        // ambil data dari form
        String nim = nimField.getText().trim();
        String nama = namaField.getText().trim();
        String jenisKelamin = (String) jenisKelaminComboBox.getSelectedItem();
        int usia = usiaSlider.getValue();
        String kelas = kelasC1Button.isSelected() ? "C1" : kelasC2Button.isSelected() ? "C2" : "";

        // validasi input
        if (nim.isEmpty() || nama.isEmpty() || jenisKelamin == null || kelas.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Harap isi semua field!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // query insert ke database
        String query = "INSERT INTO mahasiswa (nim, nama, jenis_kelamin, usia, kelas) VALUES (?, ?, ?, ?, ?)";

        try {
            // jalankan query
            database.executeUpdate(query, nim, nama, jenisKelamin, usia, kelas);

            // perbarui tabel
            mahasiswaTable.setModel(setTable());

            // kosongkan form
            clearForm();

            // tampilkan notifikasi berhasil
            JOptionPane.showMessageDialog(null, "Data berhasil ditambahkan");
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error: Gagal menambahkan data");
        }
    }

    public void updateData() {
        // ambil data dari form
        String nim = nimField.getText().trim();
        String nama = namaField.getText().trim();
        String jenisKelamin = (String) jenisKelaminComboBox.getSelectedItem();
        int usia = usiaSlider.getValue();
        String kelas = kelasC1Button.isSelected() ? "C1" : kelasC2Button.isSelected() ? "C2" : "";

        // validasi input
        if (nim.isEmpty() || nama.isEmpty() || jenisKelamin == null || kelas.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Harap isi semua field!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // query update ke database
        String query = "UPDATE mahasiswa SET nama=?, jenis_kelamin=?, usia=?, kelas=? WHERE nim=?";

        try {
            // jalankan query
            database.executeUpdate(query, nama, jenisKelamin, usia, kelas, nim);

            // perbarui tabel
            mahasiswaTable.setModel(setTable());

            // kosongkan form
            clearForm();

            // tampilkan notifikasi berhasil
            JOptionPane.showMessageDialog(null, "Data berhasil diubah");
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error: Gagal mengubah data");
        }
    }

    public void deleteData() {
        // tampilkan dialog konfirmasi
        int confirm = JOptionPane.showConfirmDialog(
                null, "apakah anda yakin ingin menghapus data ini?",
                "konfirmasi hapus", JOptionPane.YES_NO_OPTION);

        // jika pengguna memilih "yes"
        if (confirm == JOptionPane.YES_OPTION) {
            String nim = nimField.getText();
            String query = "DELETE FROM mahasiswa WHERE nim=?";

            try {
                // jalankan query
                database.executeUpdate(query, nim);

                // perbarui tabel
                mahasiswaTable.setModel(setTable());

                // kosongkan form
                clearForm();

                // tampilkan notifikasi berhasil
                JOptionPane.showMessageDialog(null, "data berhasil dihapus");
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "error: gagal menghapus data");
            }
        }
    }

    public final DefaultTableModel setTable() {
        // tentukan nama kolom tabel
        Object[] column = { "no", "nim", "nama", "jenis kelamin", "usia", "kelas" };
        DefaultTableModel temp = new DefaultTableModel(column, 0);

        try {
            // jalankan query select dari database
            ResultSet rs = database.selectQuery("SELECT * FROM mahasiswa");

            int i = 0;
            while (rs.next()) {
                // tambahkan data ke dalam tabel
                Object[] row = {
                        ++i, rs.getString("nim"), rs.getString("nama"),
                        rs.getString("jenis_kelamin"), rs.getInt("usia"), rs.getString("kelas")
                };
                temp.addRow(row);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return temp;
    }

    public void clearForm() {
        // kosongkan semua texfield, combo box, slider, dan radio button
        nimField.setText("");
        namaField.setText("");
        jenisKelaminComboBox.setSelectedItem("");
        usiaSlider.setValue(18); // default usia
        // usiaValueLabel akan otomatis diupdate oleh ChangeListener
        kelasButtonGroup.clearSelection();

        // ubah button "Update" menjadi "Add"
        addUpdateButton.setText("Add");
        // sembunyikan button delete
        deleteButton.setVisible(false);
        // ubah selectedIndex menjadi -1 (tidak ada baris yang dipilih)
        selectedIndex = -1;
    }
}