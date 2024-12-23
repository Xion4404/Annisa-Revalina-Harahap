import java.sql.*; // Import library untuk JDBC
import java.sql.Date; 
import java.time.*; // Import library untuk manipulasi tanggal
import java.util.*;


// Interface untuk operasi CRUD dasar
public class BakerManagement {
    // Parameter koneksi database
    private static final String URL = "jdbc:postgresql://localhost:5433/BakerManagement"; 
    private static final String USER = "postgres"; // Username database
    private static final String PASSWORD = "XionoiX4404"; // Password database

    interface ProductCRUD {
        // Metode abstrak untuk operasi CRUD
        void createProduct(Product product); // Untuk membuat produk baru
        List<Product> readProducts(); // Untuk membaca semua produk
        void updateProduct(int productId, int newStock); // Untuk memperbarui stok produk
        void deleteProduct(int productId); // Untuk menghapus produk
    }

    // Superclass: Product
    static class Product {
        protected int id; // ID produk
        protected String name; // Nama produk
        protected String color1; // Warna utama produk
        protected String color2; // Warna tambahan produk
        protected String size; // Ukuran produk
        protected String type; // Tipe produk
        protected int stock; // Stok produk
        protected LocalDate createdDate; // Tanggal pembuatan produk

        public Product(int id, String name, String color1, String color2, String size, String type, int stock) {
            this.id = id; // Inisialisasi elemen
            this.name = name; 
            this.color1 = color1; 
            this.color2 = color2; 
            this.size = size; 
            this.type = type; 
            this.stock = stock; 
            this.createdDate = LocalDate.now(); 
        }

        @Override
        public String toString() {
            // Mengembalikan informasi produk dalam format string
            return String.format("ID: %d, Nama: %s, Warna: %s & %s, Ukuran: %s, Tipe: %s, Stok: %d, Tanggal: %s",
                    id, name, color1, color2, size, type, stock, createdDate);
        }
    }

    // Subclass: SpecialProduct
    static class SpecialProduct extends Product {
        private String specialFeature; // Fitur khusus untuk produk spesial

        public SpecialProduct(int id, String name, String color1, String color2, String size, String type, int stock, String specialFeature) {
            super(id, name, color1, color2, size, type, stock); // Memanggil konstruktor superclass
            this.specialFeature = specialFeature; // Inisialisasi fitur khusus
        }

        @Override
        public String toString() {
            // Menambahkan fitur khusus ke informasi produk
            return super.toString() + ", Fitur Khusus: " + specialFeature;
        }
    }

    // Implementasi operasi CRUD
    static class ProductManager implements ProductCRUD {
        private Connection conn; // Objek koneksi ke database

        public ProductManager(String dbUrl, String user, String password) throws SQLException {
            conn = DriverManager.getConnection(dbUrl, user, password); // Membuat koneksi database
            ensureTableExists(); // Memastikan tabel database ada
        }

        // Memastikan tabel database tersedia
        private void ensureTableExists() {
            try (Statement stmt = conn.createStatement()) {
                String createTableSQL = "CREATE TABLE IF NOT EXISTS products (" +
                        "id SERIAL PRIMARY KEY," + // 
                        "name VARCHAR(100)," + // Kolom nama produk
                        "color1 VARCHAR(50)," + // Kolom warna1
                        "color2 VARCHAR(50)," + // Kolom warna2
                        "size VARCHAR(10)," + // Kolom ukuran
                        "type VARCHAR(50)," + // Kolom tipe
                        "stock INT," + // Kolom stok
                        "created_date DATE" + // Kolom tanggal pembuatan
                        ")";
                stmt.execute(createTableSQL); // Eksekusi perintah SQL untuk membuat tabel
            } catch (SQLException e) {
                System.err.println("Error memastikan tabel ada: " + e.getMessage()); // Menampilkan error jika tabel gagal dibuat
            }
        }

        @Override
        public void createProduct(Product product) {
            try {
                // Query untuk menambahkan data 
                String query = "INSERT INTO products (name, color1, color2, size, type, stock, created_date) VALUES (?, ?, ?, ?, ?, ?, ?)";
                PreparedStatement ps = conn.prepareStatement(query); // Membuat statement yang telah dipersiapkan
                ps.setString(1, product.name); // Mengisi parameter elemen
                ps.setString(2, product.color1); 
                ps.setString(3, product.color2); 
                ps.setString(4, product.size); 
                ps.setString(5, product.type); 
                ps.setInt(6, product.stock); 
                ps.setDate(7, Date.valueOf(product.createdDate)); 
                ps.executeUpdate(); // Menjalankan query
                System.out.println("Produk berhasil ditambahkan."); // Notifikasi berhasil
            } catch (SQLException e) {
                System.err.println("Error menambah produk: " + e.getMessage()); // Menampilkan error
            }
        }

        @Override
        public List<Product> readProducts() {
            List<Product> products = new ArrayList<>(); // Membuat daftar untuk menyimpan produk
            try {
                String query = "SELECT * FROM products"; // Query untuk membaca semua produk
                Statement stmt = conn.createStatement(); // Membuat statement eksekusi 
                ResultSet rs = stmt.executeQuery(query); // Menjalankan menyimpan hasilnya
                while (rs.next()) {
                    // Menambahkan setiap produk ke daftar
                    products.add(new Product(
                            rs.getInt("id"), // Membaca elmen
                            rs.getString("name"), 
                            rs.getString("color1"), 
                            rs.getString("color2"), 
                            rs.getString("size"), 
                            rs.getString("type"), 
                            rs.getInt("stock")));
                }
            } catch (SQLException e) {
                System.err.println("Error membaca produk: " + e.getMessage()); // Menampilkan error 
            }
            return products; // Mengembalikan daftar produk
        }

        @Override
        public void updateProduct(int productId, int newStock) {
            try {
                String query = "UPDATE products SET stock = ? WHERE id = ?"; // memperbarui stok produk
                PreparedStatement ps = conn.prepareStatement(query); 
                ps.setInt(1, newStock); // Mengisi  stok baru
                ps.setInt(2, productId); // Mengisi  ID produk
                ps.executeUpdate(); // Menjalankan query
                System.out.println("Produk berhasil diperbarui."); // Notifikasi berhasil
            } catch (SQLException e) {
                System.err.println("Error memperbarui produk: " + e.getMessage()); // Menampilkan error 
            }
        }

        @Override
        public void deleteProduct(int productId) {
            try {
                String query = "DELETE FROM products WHERE id = ?"; // menghapus produk
                PreparedStatement ps = conn.prepareStatement(query); // 
                ps.setInt(1, productId); // 
                ps.executeUpdate(); // Menjalankan query
                System.out.println("Produk berhasil dihapus."); // Notifikasi berhasil
            } catch (SQLException e) {
                System.err.println("Error menghapus produk: " + e.getMessage()); // Menampilkan error 
            }
        }

        // Contoh exception handling untuk membaca produk berdasarkan ID
        public Product findProductById(int productId) {
            try {
                String query = "SELECT * FROM products WHERE id = ?"; // mencari produk berdasarkan ID
                PreparedStatement ps = conn.prepareStatement(query); 
                ps.setInt(1, productId); 
                ResultSet rs = ps.executeQuery(); // Menjalankan 
                if (rs.next()) {
                    // Mengembalikan produk jika ditemukan
                    return new Product(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("color1"),
                            rs.getString("color2"),
                            rs.getString("size"),
                            rs.getString("type"),
                            rs.getInt("stock"));
                } else {
                    System.out.println("Produk tidak ditemukan."); // Notifikasi produk tidak ditemukan
                }
            } catch (SQLException e) {
                System.err.println("Error mencari produk: " + e.getMessage()); // Menampilkan error 
            }
            return null; // Mengembalikan null jika produk tidak ditemukan
        }
    }

    public static void main(String[] args) {
        try {
            ProductManager pm = new ProductManager(URL, USER, PASSWORD); 
            Scanner scanner = new Scanner(System.in); // Membuat scanner untuk input

            while (true) {
                // Menu utama aplikasi
                System.out.println("\n1. Tambah Produk\n2. Lihat Stok\n3. Perbarui Stok\n4. Hapus Produk\n5. Cari Produk\n6. Keluar");
                System.out.print("Pilih opsi: ");
                int choice = scanner.nextInt(); // Membaca pilihan dari pengguna
                scanner.nextLine(); // Membersihkan newline

                switch (choice) {
                    case 1:
                        // Tambah produk baru
                        System.out.print("Masukkan Nama Produk: ");
                        String name = scanner.nextLine();
                        System.out.print("Masukkan Warna 1: ");
                        String color1 = scanner.nextLine();
                        System.out.print("Masukkan Warna 2: ");
                        String color2 = scanner.nextLine(); 
                        System.out.print("Masukkan Ukuran: ");
                        String size = scanner.nextLine(); 
                        System.out.print("Masukkan Tipe: ");
                        String type = scanner.nextLine(); 
                        System.out.print("Masukkan Stok Awal: ");
                        int stock = scanner.nextInt();

                        // Membuat produk baru
                        Product newProduct = new Product(0, name, color1, color2, size, type, stock);
                        pm.createProduct(newProduct); // Menambahkan produk ke database
                        break;

                    case 2:
                        // Lihat semua produk
                        List<Product> products = pm.readProducts(); // Membaca daftar produk
                        System.out.println("\nDaftar Produk:");
                        for (Product product : products) {
                            System.out.println(product); // Menampilkan setiap produk
                        }
                        break;

                    case 3:
                        // Perbarui stok produk
                        System.out.print("Masukkan ID Produk: ");
                        int productIdToUpdate = scanner.nextInt(); // Input ID produk
                        System.out.print("Masukkan Stok Baru: ");
                        int newStock = scanner.nextInt(); // Input stok baru
                        pm.updateProduct(productIdToUpdate, newStock); // Memperbarui stok produk
                        break;

                    case 4:
                        // Hapus produk
                        System.out.print("Masukkan ID Produk: ");
                        int productIdToDelete = scanner.nextInt(); // Input ID produk
                        pm.deleteProduct(productIdToDelete); // Menghapus produk dari database
                        break;

                    case 5:
                        // Cari produk berdasarkan ID
                        System.out.print("Masukkan ID Produk: ");
                        int productIdToFind = scanner.nextInt(); // Input ID produk
                        Product foundProduct = pm.findProductById(productIdToFind); // Mencari produk
                        if (foundProduct != null) {
                            System.out.println("\nProduk Ditemukan:");
                            System.out.println(foundProduct); // Menampilkan produk 
                        }
                        break;

                    case 6:
                        // Keluar dari aplikasi
                        System.out.println("Keluar dari aplikasi. Terima kasih!");
                        scanner.close(); // Menutup scanner
                        return;

                    default:
                        // Jika input tidak valid
                        System.out.println("Pilihan tidak valid. Silakan coba lagi.");
                }
            }
        } catch (SQLException e) {
            System.err.println("Kesalahan koneksi database: " + e.getMessage()); // Menampilkan error jika gagal koneksi
        }
    }
}

