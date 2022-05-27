package gui;

import java.awt.Color;
import java.awt.Desktop;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.DefaultTableModel;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import dao.SanPhamDao;
import dao.Ipml.SanPhamImpl;
import entity.SanPham;

public class ThongKeSanPhamSapHetSoLuongPnGUI extends JPanel implements ActionListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = 7262463616864319188L;
	private JTable tbHetSoLuong;
	private JButton btnXuatFile;
	private DefaultTableModel model;
	private SanPhamDao sanPhamDao;
	
	public ThongKeSanPhamSapHetSoLuongPnGUI() {
		setBackground(Color.WHITE);
		setLayout(null);
		
		setBounds(0, 0, 1364, 621);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(0, 60, 1364, 506);
		add(scrollPane);
		
		model = new DefaultTableModel(new String[] { "Mã sản phẩm", "Tên sản phẩm", "Loại sản phẩm", "Nhãn hiệu","Màu sắc","Kích thước","Chất liệu","Số lượng","Giá bán"},0){
			/**
			 * 
			 */
			private static final long serialVersionUID = 8798493760278188131L;

			@Override
			public boolean isCellEditable(int row, int column) {
				// all cells false
				return false;
			}
		};

		tbHetSoLuong = new JTable();
		tbHetSoLuong.setFont(new Font("Arial", Font.PLAIN, 14));
		tbHetSoLuong.setModel(model);
		scrollPane.setViewportView(tbHetSoLuong);
		tbHetSoLuong.setRowHeight(30);
		tbHetSoLuong.getColumnModel().getColumn(1).setPreferredWidth(200);
		tbHetSoLuong.getColumnModel().getColumn(0).setPreferredWidth(20);
		tbHetSoLuong.getColumnModel().getColumn(2).setPreferredWidth(20);
		tbHetSoLuong.getColumnModel().getColumn(4).setPreferredWidth(20);
		tbHetSoLuong.getColumnModel().getColumn(5).setPreferredWidth(20);
		tbHetSoLuong.getColumnModel().getColumn(6).setPreferredWidth(20);
		tbHetSoLuong.getColumnModel().getColumn(7).setPreferredWidth(20);

		btnXuatFile = new JButton("Xuất file excel");
		btnXuatFile.setIcon(new ImageIcon(ThongKeSanPhamSapHetSoLuongPnGUI.class.getResource("/img/excel.png")));
		btnXuatFile.setFont(new Font("Arial", Font.PLAIN, 16));
		btnXuatFile.setBounds(1183, 576, 171, 35);
		add(btnXuatFile);
		
		JLabel lblNewLabel = new JLabel("DANH SÁCH SẢN PHẨM SẮP HẾT SỐ LƯỢNG");
		lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 20));
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel.setBounds(0, 0, 1364, 50);
		add(lblNewLabel);
		
		btnXuatFile.addActionListener(this);
		
		loadData();
	}
	
	private void loadData() {
		sanPhamDao = new SanPhamImpl();
		
		model.getDataVector().removeAllElements();
		model.fireTableDataChanged();
		tbHetSoLuong.clearSelection();
		
		DecimalFormat format = new DecimalFormat("###,###,###,###.### VND");
		
		List<SanPham> list = sanPhamDao.getSanPhamSapHetSoLuong();
		list.forEach(sp -> {
			model.addRow(new Object[] {sp.getMaSP(),sp.getTenSP(),sp.getLoaiSanPham().getTenLoai(),sp.getNhanHieu(),sp.getMauSac(),sp.getKichThuoc(),sp.getChatLieu(),sp.getSoLuong(),format.format(sp.getDonGia())});
		});
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object o = e.getSource();
		
		if (btnXuatFile == o) {
			if (tbHetSoLuong.getRowCount() == 0)
				JOptionPane.showMessageDialog(this, "Không có nội dung để xuất!");
			else
				handleXuatFile();
		}
		
	}
	
	private void handleXuatFile() {
		JFileChooser fChooser = new JFileChooser();

		fChooser.setCurrentDirectory(new File("C:\\Users\\Trung Ngoc\\Downloads"));
		fChooser.addChoosableFileFilter(new FileFilter() {

			@Override
			public String getDescription() {
				// TODO Auto-generated method stub
				return "Excel file (*.xls, *xlsx)";
			}

			@Override
			public boolean accept(File f) {
				// TODO Auto-generated method stub
				if (f.isDirectory()) {
					return true;
				} else {
					return f.getName().toLowerCase().endsWith(".xls") || f.getName().toLowerCase().endsWith(".xlsx");
				}
			}
		});

		int i = fChooser.showSaveDialog(this);
		if (i == 0) {
			String path = fChooser.getSelectedFile().getAbsolutePath();

			if (!path.matches("(.)+(\\.xls|\\.xlsx)$")) {
				path += ".xlsx";
			}
			boolean t = true;
			t = ghiFile(path);
			if (t) {
				int xacNhan = JOptionPane.showConfirmDialog(this, "Bạn có muốn xem file", "Thông báo",
						JOptionPane.YES_NO_OPTION);
				if (xacNhan == JOptionPane.YES_OPTION)
					try {
						Desktop.getDesktop().open(new File(path));
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
			} else
				JOptionPane.showMessageDialog(this, "Không thành công");

		}
	}

	@SuppressWarnings("resource")
	private boolean ghiFile(String path) {
		Workbook workBook = new XSSFWorkbook();

		Sheet sh = workBook.createSheet("Sheet1");
		String header[] = { "Mã sản phẩm", "Tên sản phẩm","Loại sản phẩm","Nhãn hiệu", "Kích thước", "Màu sắc", "Chất liệu","Số lượng",
				"Đơn giá"};

		String title = "                                                                 NPK FASHION - BẢNG THỐNG KÊ DANH SÁCH SẢN PHẨM SẮP HẾT SỐ LƯỢNG";
		
		sh.addMergedRegion(new CellRangeAddress(0, 0, 0, 8));
		sh.createRow(0).createCell(0).setCellValue(title);

		sh.createRow(1);
		Row rowHeader = sh.createRow(2);
		for (int i = 0; i < header.length; i++) {
			Cell cell = rowHeader.createCell(i);
			cell.setCellValue(header[i]);
		}

		int numRow = 3;
		for (; numRow < tbHetSoLuong.getRowCount()+3; numRow++) {
			Row row = sh.createRow(numRow);
			row.createCell(0).setCellValue(tbHetSoLuong.getValueAt(numRow - 3, 0).toString().trim());
			row.createCell(1).setCellValue(tbHetSoLuong.getValueAt(numRow - 3, 1).toString().trim());
			row.createCell(2).setCellValue(tbHetSoLuong.getValueAt(numRow - 3, 2).toString().trim());
			row.createCell(3).setCellValue(tbHetSoLuong.getValueAt(numRow - 3, 3).toString().trim());
			row.createCell(4).setCellValue(tbHetSoLuong.getValueAt(numRow - 3, 4).toString().trim());
			row.createCell(5).setCellValue(tbHetSoLuong.getValueAt(numRow - 3, 5).toString().trim());
			row.createCell(6).setCellValue(tbHetSoLuong.getValueAt(numRow - 3, 6).toString().trim());
			row.createCell(7).setCellValue(tbHetSoLuong.getValueAt(numRow - 3, 7).toString().trim());
			row.createCell(8).setCellValue(tbHetSoLuong.getValueAt(numRow - 3, 8).toString().trim());
		}

		for (int i = 0; i < header.length; i++) {
			sh.autoSizeColumn(i);
		}

		try {
			FileOutputStream f = new FileOutputStream(path);
			workBook.write(f);
			f.close();
			workBook.close();
		} catch (Exception e) {
			return false;
		}
		return true;
	}

}
