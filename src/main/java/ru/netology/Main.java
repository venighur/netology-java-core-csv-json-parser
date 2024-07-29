package ru.netology;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException, ParserConfigurationException, SAXException {
        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
        String fileNameCSV = "data.csv";
        String fileNameXML = "data.xml";

        List<Employee> listFromCSV = parseCSV(columnMapping, fileNameCSV);
        List<Employee> listFromXML = parseXML(fileNameXML);

        String jsonFromCSV = listToJson(listFromCSV);
        String jsonFromXML = listToJson(listFromXML);

        writeString(jsonFromCSV, "data.json");
        writeString(jsonFromXML, "data2.json");
    }

    public static List<Employee> parseCSV(String[] columns, String file) {
        try (CSVReader reader = new CSVReader(new FileReader(file))) {
            ColumnPositionMappingStrategy<Employee> strategy = new ColumnPositionMappingStrategy<>();
            strategy.setType(Employee.class);
            strategy.setColumnMapping(columns);
            CsvToBean<Employee> csv = new CsvToBeanBuilder<Employee>(reader)
                    .withMappingStrategy(strategy)
                    .build();
            return csv.parse();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<Employee> parseXML(String file) throws IOException, SAXException, ParserConfigurationException {
        List<Employee> employees = new ArrayList<>();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new File(file));
        Node staff = doc.getDocumentElement();
        NodeList nodeList = staff.getChildNodes();

        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (Node.ELEMENT_NODE == node.getNodeType()) {
                Element elem = (Element) node;
                String id = elem.getElementsByTagName("id").item(0).getTextContent();
                String firstName = elem.getElementsByTagName("firstName").item(0).getTextContent();
                String lastName = elem.getElementsByTagName("lastName").item(0).getTextContent();
                String country = elem.getElementsByTagName("country").item(0).getTextContent();
                String age = elem.getElementsByTagName("age").item(0).getTextContent();
                employees.add(new Employee(Integer.parseInt(id), firstName, lastName, country, Integer.parseInt(age)));
            }
        }

        return employees;
    }

    public static String listToJson(List<Employee> list) {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        Type listType = new TypeToken<List<Employee>>() {}.getType();

        return gson.toJson(list, listType);
    }

    public static void writeString(String json, String fileName) {
        try (FileWriter file = new FileWriter(fileName)) {
            file.write(json);
            file.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}