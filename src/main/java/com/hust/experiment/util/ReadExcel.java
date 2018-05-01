package com.hust.experiment.util;



import com.hust.experiment.model.Student;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class ReadExcel {

    private File file = null;
    private Sheet sheet = null;
    private List<Student> list = new ArrayList<>();

    public ReadExcel(File file){
        this.file = file;
    }

    public void loadExcel(){
        try{
            XSSFWorkbook workbook = new XSSFWorkbook(file);
            sheet = workbook.getSheetAt(0);
            for(int ri = sheet.getFirstRowNum()+1;ri < sheet.getLastRowNum();ri++){
                Row row = sheet.getRow(ri);
                Student student = new Student();
                Double className = (Double)getCellValue(row.getCell(0));
                student.setClassname(Integer.toString(className.intValue()));
                student.setName((String)getCellValue(row.getCell(1)));
                student.setStuId((String)getCellValue(row.getCell(2)));
                student.setAcademy((String)getCellValue(row.getCell(3)));
                list.add(student);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public List<Student> getStudentList(){
        return this.list;
    }

    private Object getCellValue(Cell cell){
        int type = cell.getCellType();
        switch(type){
            case Cell.CELL_TYPE_BLANK:
                return "null";
                case Cell.CELL_TYPE_BOOLEAN:
                    return cell.getBooleanCellValue();
                    case Cell.CELL_TYPE_ERROR:
                        return cell.getErrorCellValue();
                        case Cell.CELL_TYPE_FORMULA:
                            return cell.getNumericCellValue();
                            case Cell.CELL_TYPE_NUMERIC:
                                return cell.getNumericCellValue();
                                case Cell.CELL_TYPE_STRING:
                                    return cell.getStringCellValue();
                                    default:return cell.getStringCellValue();
        }
    }

}
