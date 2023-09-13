package com.astontech.lms.bootstrap;

import com.astontech.lms.domain.Course;
import com.astontech.lms.domain.Path;
import com.astontech.lms.domain.Video;
import com.astontech.lms.repositories.PathRepository;
import com.astontech.lms.services.CourseService;
import com.astontech.lms.services.PathService;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.*;

@Component
public class ExcelReader {

    // DEPRECIATED - THIS FUNCTIONALITY HAS MOVED TO THE FRONT-END

    /*
       =========================================================================================
       Example code:  https://github.com/callicoder/java-read-write-excel-file-using-apache-poi
       =========================================================================================
    */

    public static final String SAMPLE_XLSX_FILE_PATH = "./java-course-data.xlsx";

    PathRepository pathRepository;
    CourseService courseService;

    public ExcelReader(PathRepository pathRepository, CourseService courseService) {
        this.pathRepository = pathRepository;
        this.courseService = courseService;
    }

    public void importJavaPathFromExcel() throws IOException, InvalidFormatException {

        // Creating a Workbook from an Excel file (.xls or .xlsx)
        Workbook workbook = WorkbookFactory.create(new File(SAMPLE_XLSX_FILE_PATH));

        // Getting the Sheet at index zero
        Sheet sheet = workbook.getSheetAt(0);

        // Create a DataFormatter to format and get each cell's value as String
        DataFormatter dataFormatter = new DataFormatter();
        Path javaPath = null;
        List<String> courseTitleList = new ArrayList<>();

        Iterator<Row> rowIterator = sheet.rowIterator();

        Row row = rowIterator.next();
        while (rowIterator.hasNext()) {

            String rowType = dataFormatter.formatCellValue(row.getCell(0));
            // first row should be path
            if("PATH".equals(rowType)) {
                javaPath = new Path(row.getCell(1).toString(),  // Title
                                    row.getCell(2).toString(),  // Short Title
                                    row.getCell(3).toString()); // Image
                row = rowIterator.next();
                continue;
            }

            if("COURSE".equals(rowType)) {
                Course course = new Course();
                course.setTitle(row.getCell(1).toString());
                course.setDesc(row.getCell(2).toString());
                course.setCourseImg(row.getCell(3).toString());

                // initialize video list
                List<Object> videoLabsAndQuizList = new ArrayList<>();

                // move to next row and get type
                row = rowIterator.next();
                rowType = dataFormatter.formatCellValue(row.getCell(0));

                //loop until next "COURSE" is found
                while (!"COURSE".equals(rowType)) {
                    Video videoOrLab = new Video();

                    if("VIDEO".equals(rowType)) {
                        videoOrLab.setTitle(row.getCell(1).toString());
                        videoOrLab.setDescription(row.getCell(2).toString());
                        videoOrLab.setURL(row.getCell(3).toString());
                        videoOrLab.setContentType("VIDEO");
                        videoLabsAndQuizList.add(videoOrLab);

                    } else if ("LAB".equals(rowType)) {
                        videoOrLab.setTitle(row.getCell(1).toString());
                        videoOrLab.setURL(row.getCell(3).toString());
                        videoOrLab.setContentType("LAB");
                        videoLabsAndQuizList.add(videoOrLab);

                    } else {
                        System.out.println("TYPE error.  unknown TYPE, must be VIDEO or LAB");
                    }


                    // move to next row and get type
                    if(rowIterator.hasNext()) {
                        row = rowIterator.next();
                        rowType = dataFormatter.formatCellValue(row.getCell(0));
                    } else {
                        break;
                    }
                }

                // add all videos and labs to course
                course.setContent(videoLabsAndQuizList);
                // save course
                String courseTitle = courseService.save(course).getTitle();
                courseTitleList.add(courseTitle);
            }
        }

        javaPath.setCourseTitles(courseTitleList);
        pathRepository.save(javaPath);
        // Closing the workbook
        workbook.close();
    }


}