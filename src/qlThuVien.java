import java.sql.DriverManager;
import java.sql.Connection;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.CallableStatement;
import java.util.Calendar;

public class qlThuVien {
    public static void main(String args[]) {
        try {
            //1. Nối kết cơ sở dữ liệu
            
            String connectStr = "jdbc:mysql://localhost/thuvien?user=root";
            Connection connect = DriverManager.getConnection(connectStr);
            try {
                System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out), true, "UTF-8"));
            } catch (UnsupportedEncodingException ex) {
                Logger.getLogger(qlThuVien.class.getName()).log(Level.SEVERE, null, ex);
            }
            System.out.println("Nối kết thành công!");
            
            //2. Tạo menu
            
            String menu[] = {
                    "Đăng ký làm thẻ thư viện",
                    "Xem danh sách các quyển sách trong thư viện",
                    "Mượn sách",
                    "Xem danh sách các đọc giả",
                    "Xem danh sách các đọc giả mượn sách",
                    "Xem danh sách các đọc giả đã trả sách",
                    "Xem danh sách các quyển sách chưa được trả",
                    "Xem danh sách các quyển sách không được mượn",
                    "Thêm sách mới",
                    "Xóa đọc giả",
                    "Xóa sách cũ",
                    "Tìm kiếm sách",
                    "Trả sách",
                    "Tổng kết cuối tháng",
                    "Thoát."                
            };
            int opt;
            boolean condition = false;
            ResultSet result = null;
            ResultSet rs = null;
            Statement stm;
            CallableStatement stmt = null;
            CallableStatement statement = null;
            do {
                System.out.println(" ");
                System.out.println("\t\t\t____________________MENU_________________________");
                for(int i = 0 ; i < menu.length; i++) {
                    System.out.println("\t\t\t " +(i+1) +". " + menu[i]);          
                } 
                System.out.println("Vui lòng nhập lựa chọn của bạn?");            
                Scanner k = new Scanner(System.in);
                System.out.print("Lựa chọn của bạn là: ");
                int option = k.nextInt();
                k.nextLine();                
                Scanner sc = new Scanner(System.in);

                switch(option) {
                    case 1: 
                        System.out.println("\t\t---------------------------------------------------------------------------------");
                        System.out.println("Vui lòng nhập thông tin đọc giả: ");
                        System.out.print("Họ tên đọc giả: ");
                        String tenDG = k.nextLine();
                        System.out.print("Giới tính: M (Male) - F (Female):  ");
                        String gioiTinh = k.nextLine();
                        System.out.print("Ngày sinh (YYYY-MM-DD): ");
                        String a = k.nextLine();
                        Date ngaySinh = Date.valueOf(a);
                        System.out.print("Địa chỉ: ");
                        String diaChi = k.nextLine();                       
                        stmt = connect.prepareCall("{call spAddUser(?,?,?,?)}");
                        stmt.setString(1, tenDG);
                        stmt.setString(2, gioiTinh);
                        stmt.setDate(3, ngaySinh);
                        stmt.setString(4, diaChi);
                        stmt.executeUpdate();
                        System.out.println("\t\t\t\tThêm đọc giả thành công");
                        System.out.println("--------------------------------------------------------");

                            break;

                    case 2:
                        System.out.println("--------------------------------------------------------------------------------------------");                       
                        
                        stmt = connect.prepareCall("{call spListbook()}");
                        rs = stmt.executeQuery();
                        System.out.println("\t\t\t\t\t\t\t\t DANH SÁCH CÁC QUYỂN SÁCH:");
                        System.out.println("\t\t\t_________________________________________________________________________________________________________");
                        System.out.println("\t\t\t| Mã sách |          Tên sách           |         Thể loại         | Số lượng |       Tên tác giả       |");
                        System.out.println("\t\t\t|_________|_____________________________|__________________________|__________|_________________________|");
                        while (rs.next()){
                            System.out.printf("\t\t\t| %-7s | %-27s | %-24s | %-8s | %-23s |", rs.getInt("S_id"), rs.getString("S_ten")
                                    , rs.getString("S_theloai"), rs.getString("S_soluong"), rs.getString("TG_ten"));
                            System.out.printf("\n");
                            System.out.println("\t\t\t|_________|_____________________________|__________________________|__________|_________________________|");
                        }
                        System.out.println("--------------------------------------------------------------------------------------------");
                        break;
                    case 3:
                        System.out.println("--------------------------------------------------------------------------------------------");
                        System.out.println("Vui lòng điền thông tin sau: ");
                        System.out.print("Mã đọc giả: ");
                        Integer maDG = sc.nextInt();
                        sc.nextLine();
                        Boolean continute = true;
                        int c;
                        do {
                            System.out.print("Tên quyển sách muốn mượn: ");
                            String name = sc.nextLine();
                            System.out.print("Số lượng: ");
                            Integer sl = sc.nextInt();
                            sc.nextLine();
                            System.out.print("Hạn trả sách(yyyy-mm-dd): ");
                            String date = sc.nextLine();
                            Date hantra = Date.valueOf(date);
                            Date ngaymuon = new Date(System.currentTimeMillis());

                            Boolean check = false;
                            do {
                                PreparedStatement ctmt = null;
                                ResultSet rs1 = null;
                                ctmt = connect.prepareStatement("select fnCheckBook(?, ?) as cbook");
                                ctmt.setString(1, name);
                                ctmt.setInt(2, sl);
                                rs1 = ctmt.executeQuery();
                                rs1.next();
                                if ("none".equals(rs1.getString("cbook"))){
                                    System.out.println("\t\t\t\t\tSách này hiện không có trong thư viện!! Vui lòng nhập lại");
                                    System.out.print("Tên quyển sách muốn mượn: ");
                                    name = sc.nextLine();
                                } else {
                                    if ("thiếu".equals(rs1.getString("cbook"))) {
                                        System.out.println("\t\t\t\t\tSố lượng sách không đủ !! Vui lòng nhập lại ");
                                        System.out.print("Số lượng: ");
                                        sl = sc.nextInt();
                                    } else if ("hết".equals(rs1.getString("cbook"))) {
                                        System.out.println("\t\t\t\t\tSách này đã được mượn hết !!");
                                        System.out.println("Chọn: (1) Để mượn quyển khác, (2) Quay trở lại sau ");
                                        int nhap = sc.nextInt();
                                        sc.nextLine();
                                        if (nhap == 1) {
                                            System.out.print("Tên quyển sách muốn mượn: ");
                                            name = sc.nextLine();
                                            System.out.print("Số lượng: ");
                                            sl = sc.nextInt();
                                        } else {
                                            System.out.print("\t\t\t\t\tCám ơn bạn đã đến thư viện");
                                            check = false;
                                            continute = false;
                                            break;
                                        }
                                    } else {
                                        check = true;
                                        break;
                                    }
                                }
                            } while (check != true);

                           
                            stmt = connect.prepareCall("{call spMuonsach(?,?,?,?,?)}");
                            stmt.setInt(1, maDG);
                            stmt.setString(2, name);
                            stmt.setInt(3, sl);
                            stmt.setDate(4, hantra);
                            stmt.setDate(5, ngaymuon);
                            rs = stmt.executeQuery();
                            if (rs != null)
                                System.out.println("\t\t\t\t\tMượn sách thành công !!!!");
                            System.out.println("Bạn có muốn tiếp tục mượn sách ? (1):Tiếp tục, (2):Dừng ");
                            c = sc.nextInt();
                            if (c == 2)
                                continute = false;
                        } while (continute == true);
                        System.out.println("--------------------------------------------------------------------------------------------");
                        break;   

                    case 4:
                       
                        stmt = connect.prepareCall("{call spDSDG()}");  
                        result = stmt.executeQuery();   
                        System.out.println("\t\t\t\t\t\tBẢNG DANH SÁCH CÁC ĐỌC GIẢ"); 
                        System.out.println("\t\t_____________________________________________________________________________________");
                        System.out.println("\t\t| Mã đọc giả |        Tên đọc giả        | Giới tính |  Ngày sinh  |     Địa chỉ    |");
                        System.out.println("\t\t|____________|___________________________|___________|_____________|________________|");
                        while(result.next()) { 
                            System.out.printf("\t\t|     %-6s |    %-22s |     %-5s | %-11s |     %-10s |", result.getString("DG_id"),result.getString("DG_ten"),result.getString("DG_gioitinh"),result.getString("DG_ngaysinh"),result.getString("DG_diachi"));            
                            System.out.printf("\n");
                            System.out.println("\t\t|____________|___________________________|___________|_____________|________________|");
                        }
                        System.out.println("---------------------------------------------------------------------------------------------------------------------");
                        break;

                    case 5:
                               
                        stmt = connect.prepareCall("{call spDSDGMS()}");  
                        result = stmt.executeQuery(); 
                       // System.out.println("--------------------------------------------------------");
                        System.out.println("\t\t\t\t\tBẢNG DANH SÁCH CÁC ĐỌC GIẢ MƯỢN SÁCH"); 
                        System.out.println("\t\t________________________________________________________________________________________________");
                        System.out.println("\t\t|        Tên đọc giả        |         Tên sách             |  Ngày mượn sách  |  Hạn trả sách  |");
                        System.out.println("\t\t|___________________________|______________________________|__________________|________________|");                    
                        while(result.next()) {
                            System.out.printf("\t\t|    %-22s | %-28s |   %-14s |   %-13s|", result.getString("DG_ten"), result.getString("S_ten"), result.getString("M_ngaymuon"),result.getString("M_hantra"));            
                            System.out.printf("\n");
                            System.out.println("\t\t|___________________________|______________________________|__________________|________________|"); 

                        }
                        System.out.println("---------------------------------------------------------------------------------------------------------------------");
                        break;

                    case 6:

                        stmt = connect.prepareCall("{call spPayBook()}");
                        result = stmt.executeQuery();
                        System.out.println("\t\t\t\tBẢNG DANH SÁCH CÁC ĐỌC GIẢ ĐÃ TRẢ SÁCH"); 
                        System.out.println("\t\t______________________________________________________________________________");
                        System.out.println("\t\t|        Tên đọc giả        |         Tên sách             |  Ngày trả sách  |");
                        System.out.println("\t\t|___________________________|______________________________|_________________|");                  
                        while(result.next()) {
                            System.out.printf("\t\t|    %-22s | %-28s |   %-13s |", result.getString("DG_ten"), result.getString("S_ten"), result.getString("M_hantra"));            
                            System.out.printf("\n");
                            System.out.println("\t\t|___________________________|______________________________|_________________|");           
                        }     
                        System.out.println("---------------------------------------------------------------------------------------------------------------------");
                        break;
                        
                    case 7:

                        stmt = connect.prepareCall("{call spNoPayBook()}");
                        result = stmt.executeQuery();
                        System.out.println("\t\t\t\tBẢNG DANH SÁCH CÁC ĐỌC GIẢ CHƯA TRẢ SÁCH"); 
                        System.out.println("\t\t______________________________________________________________________________");
                        System.out.println("\t\t|        Tên đọc giả        |         Tên sách             |  Ngày trả sách  |");
                        System.out.println("\t\t|___________________________|______________________________|_________________|");          
                        while(result.next()) {
                            System.out.printf("\t\t|    %-22s | %-28s |   %-13s |", result.getString("DG_ten"), result.getString("S_ten"), result.getString("M_hantra"));            
                            System.out.printf("\n");
                            System.out.println("\t\t|___________________________|______________________________|_________________|");           
                        }         
                        System.out.println("---------------------------------------------------------------------------------------------------------------------");
                        break;
                          
                    case 8:
                        System.out.println("--------------------------------------------------------------------------------------------");                    
                        stmt = connect.prepareCall("{call spSachNotmuon()}");
                        rs = stmt.executeQuery();
                        if (rs != null){
                            System.out.println("\t\t\t\t\tDanh sách các quyển sách chưa mượn:");
                            System.out.println("\t\t\t________________________________________________________________________________________________");
                            System.out.println("\t\t\t| Mã sách |          Tên sách           |         Thể loại         | Số lượng |   Mã tác giả   |");
                            System.out.println("\t\t\t|_________|_____________________________|__________________________|__________|________________|");
                            while (rs.next()){
                                System.out.printf("\t\t\t| %-7s | %-27s | %-24s | %-8s | %-14s |", rs.getInt("S_id"), rs.getString("S_ten")
                                        , rs.getString("S_theloai"), rs.getString("S_soluong"), rs.getInt("TG_id"));
                                System.out.printf("\n");
                                System.out.println("\t\t\t|_________|_____________________________|__________________________|__________|________________|");
                            }
                            System.out.println("--------------------------------------------------------------------------------------------");
                            break;
                        } else {
                            System.out.println("\t\t\t\t\tTất cả các quyển sách trong thư viện đã được mượn !!!");
                            System.out.println("--------------------------------------------------------------------------------------------");
                            break;
                        }
                    case 9:
                        System.out.println("--------------------------------------------------------------------------------------------");
                        System.out.println("Vui lòng nhập thông tin sách mới: ");
                        System.out.print("Tên quyển sách: ");
                        String tensach = sc.nextLine();
                        System.out.print("Thể loại: ");
                        String theloai = sc.nextLine();
                        System.out.print("Số lượng: ");
                        Integer sl = sc.nextInt();
                        sc.nextLine();
                        System.out.print("Tên tác giả: ");
                        String tenTG = sc.nextLine();
                        stmt = connect.prepareCall("{call spThemsach(?,?,?,?)}");
                        stmt.setString(1, tensach);
                        stmt.setString(2, theloai);
                        stmt.setInt(3, sl);
                        stmt.setString(4, tenTG);
                        rs = stmt.executeQuery();
                        
                        if (rs != null)
                            System.out.println("\t\t\t\t\tThêm sách mới thành công");
                        else
                            System.out.println("\t\t\t\t\tThêm sách mới thất bại");
                        System.out.println("--------------------------------------------------------------------------------------------");
                        break;
                        
                    case 10: 
                        
                        System.out.print("Nhập tên đọc giả cần xóa: ");
                        String find = k.nextLine();                       
                        stmt = connect.prepareCall("{call spFindDG(?)}");
                        stmt.setString(1, find);
                        result = stmt.executeQuery();                       
                        if(result != null){
                            statement = connect.prepareCall("{call spDeleteUser(?)}");
                            statement.setString(1, find);
                            statement.executeQuery();
                           System.out.println("\t\t\t\tXoá đọc giả thành công!");  
                        } else{ 
                            System.out.println("\t\t\t\tKhông tìm thấy đọc giả!"); 
                        }                          
                            
                        break;

                   
                    case 11:
                        System.out.println("--------------------------------------------------------------------------------------------");
                        System.out.print("Vui lòng nhập tên quyển sách muốn xóa: ");
                        String tenSach = sc.nextLine();
                        stmt = connect.prepareCall("{call spXoasach(?)}");
                        stmt.setString(1, tenSach);
                        ResultSet kq = stmt.executeQuery();
                        if (kq != null)
                            System.out.println("\t\t\t\t\tXóa thành công");
                        else
                            System.out.println("\t\t\t\t\tXóa thất bại");
                        System.out.println("--------------------------------------------------------------------------------------------");
                        break;
                    case 12:
                        System.out.println("--------------------------------------------------------------------------------------------");
                        System.out.println("Vui lòng chọn phương thức tìm kiếm: ");
                        System.out.println("(1) Tìm theo tên sách, (2) Tìm theo tên tác giả, (3) Tìm theo thể loại  ");
                        System.out.print("Lựa chọn của bạn: ");
                        Integer choose = sc.nextInt();
                        sc.nextLine();
                        switch (choose) {
                            case 1:{
                                System.out.print("Mời bạn nhập tên sách: ");
                                String TenSach = sc.nextLine();

                                stmt = connect.prepareCall("{call spsearchTensach(?)}");
                                stmt.setString(1, '%'+TenSach+'%');
                                rs = stmt.executeQuery();
                                if (rs != null){
                                    System.out.println("\t\t\t\t\tDanh sách kết quả: ");
                                    System.out.println("\t\t\t________________________________________________________________________________________________________");
                                    System.out.println("\t\t\t| Mã sách |          Tên sách           |         Thể loại         | Số lượng |       Mã tác giả       |");
                                    System.out.println("\t\t\t|_________|_____________________________|__________________________|__________|________________________|");
                                    while (rs.next()){
                                        System.out.printf("\t\t\t| %-7s | %-27s | %-24s | %-8s | %-22s |", rs.getInt("S_id"), rs.getString("S_ten")
                                                , rs.getString("S_theloai"), rs.getString("S_soluong"), rs.getInt("TG_id"));
                                        System.out.printf("\n");
                                        System.out.println("\t\t\t|_________|_____________________________|__________________________|__________|________________________|");
                                    }
                                } else {
                                    System.out.println("\t\t\t\t\tThư viện hiện không có quyển sách bạn muốn tìm ");
                                }
                                break;
                            }
                            case 2:{
                                System.out.print("Mời bạn nhập tên tác giả: ");
                                String tentg = sc.nextLine();
                               
                                stmt = connect.prepareCall("{call spsearchTenTG(?)}");
                                stmt.setString(1, tentg);
                                rs = stmt.executeQuery();
                                
                                if (rs != null) {
                                    System.out.println("\t\t\t\t\tDanh sách kết quả: ");
                                    System.out.println("\t\t\t_________________________________________________________________________________________________________");
                                    System.out.println("\t\t\t| Mã sách |          Tên sách           |         Thể loại         | Số lượng |       Tên tác giả       |");
                                    System.out.println("\t\t\t|_________|_____________________________|__________________________|__________|_________________________|");
                                    while (rs.next()) {
                                        System.out.printf("\t\t\t| %-7s | %-27s | %-24s | %-8s | %-23s |", rs.getInt("S_id"), rs.getString("S_ten"), rs.getString("S_theloai")
                                                , rs.getString("S_soluong"), tentg);
                                        System.out.printf("\n");
                                        System.out.println("\t\t\t|_________|_____________________________|__________________________|__________|_________________________|");
                                    }
                                }else {
                                    System.out.println("\t\t\t\t\tThư viện hiện không có sách của tác giả " + tentg);
                                }
                                break;
                            }
                            case 3:{
                                System.out.print("Mời bạn nhập tên thể loại: ");
                                String theLoai = sc.nextLine();                               
                           
                                stmt = connect.prepareCall("{call spsearchTheloai(?)}");
                                stmt.setString(1, '%'+theLoai+'%');
                                rs = stmt.executeQuery();
                                if (rs != null){
                                    System.out.println("\t\t\t\t\tDanh sách kết quả: ");
                                    System.out.println("\t\t\t________________________________________________________________________________________________________");
                                    System.out.println("\t\t\t| Mã sách |          Tên sách           |         Thể loại         | Số lượng |       Mã tác giả       |");
                                    System.out.println("\t\t\t|_________|_____________________________|__________________________|__________|________________________|");
                                    while (rs.next()){
                                        System.out.printf("\t\t\t| %-7s | %-27s | %-24s | %-8s | %-22s |", rs.getInt("S_id"), rs.getString("S_ten")
                                                , rs.getString("S_theloai"), rs.getString("S_soluong"), rs.getInt("TG_id"));
                                        System.out.printf("\n");
                                        System.out.println("\t\t\t|_________|_____________________________|__________________________|__________|________________________|");
                                    }
                                } else {
                                    System.out.println("\t\t\t\t\tThư viện hiện không có sách thuộc thể loại " + theLoai);
                                }
                                break;
                            }
                        }
                        System.out.println("--------------------------------------------------------------------------------------------");
                        break;
                        
                    case 13:
                        System.out.println("--------------------------------------------------------------------------------------------");
                        System.out.println("Vui lòng nhập các thông tin sao để trả sách:");
                        System.out.print("Mã đọc giả: ");
                        Integer madg = sc.nextInt();
                        sc.nextLine();
                        System.out.print("Tên sách muốn trả: ");
                        String sach = sc.nextLine();
                        Date ngaytra = new Date(System.currentTimeMillis());
                        stmt = connect.prepareCall("{call spTrasach(?, ?, ?)}");
                        stmt.setInt(1, madg);
                        stmt.setString(2, sach);
                        stmt.setDate(3, ngaytra);
                        rs = stmt.executeQuery();
                        
                        PreparedStatement ctmt = null;
                        ResultSet rs1 = null;
                        ctmt = connect.prepareStatement("select * from tra");
                        rs1 = ctmt.executeQuery();
                        System.out.println("\t\t\t\t\tCám ơn bạn đã trả sách ! Hẹn gặp lại lần sau");                        
                        System.out.println("--------------------------------------------------------------------------------------------");
                           break;
                        
                    case 14:
                        Calendar calendar = Calendar.getInstance();
                        System.out.println("--------------------------------------------------------------------------------------------");
                        System.out.println("\t\t\t\t\tTổng kết cuối tháng " + (calendar.get(Calendar.MONTH)+1));
                        ctmt = connect.prepareStatement("select fnTongsachmuon() as tongmuon");
                        rs = ctmt.executeQuery();
                        rs.next();
                        System.out.println("\t\t\tTổng số sách được mượn: " + rs.getInt("tongmuon") + " quyển");
                        
                        ctmt = connect.prepareStatement("select fnTongsachton() as tongcon");
                        rs = ctmt.executeQuery();
                        rs.next();
                        System.out.println("\t\t\tTổng số sách còn lại: " + rs.getInt("tongcon") + " quyển");
                        
                        ctmt = connect.prepareStatement("select fnTongsachtra() as tongtra");
                        rs = ctmt.executeQuery();
                        rs.next();
                        System.out.println("\t\t\tTổng số sách đã trả: " + rs.getInt("tongtra") + " quyển");
                        
                        ctmt = connect.prepareStatement("select fnTongsachno() as tongno");
                        rs = ctmt.executeQuery();
                        rs.next();
                        System.out.println("\t\t\tTổng số sách chưa trả: "+ rs.getInt("tongno") + " quyển");
                        
                        System.out.println("--------------------------------------------------------------------------------------------");
                        break;
                      
                    case 15:                        
                        System.out.println("\t\t\t\t\tThoát khỏi hệ thống thành công!!!");
                        condition = true;
                        break;
                }
            }while(!condition);
        }
        catch (SQLException e) {
            System.out.println("Lỗi: " + e.getMessage());
        }
        
    } 
}
