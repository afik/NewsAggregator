package mypackage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import model.WekaClass;

public class MyForm {

    // Init ---------------------------------------------------------------------------------------

    private String text;
    private File file;
    private String[] check;
    private Map<String, Boolean> checked = new HashMap<String, Boolean>();
    private Map<String, String> errors = new HashMap<String, String>();
    private Map<String, String> messages = new HashMap<String, String>();

    // Getters ------------------------------------------------------------------------------------

    public String getText() {
        return text;
    }

    public File getFile() {
        return file;
    }

    public String[] getCheck() {
        return check;
    }

    // Setters ------------------------------------------------------------------------------------

    public void setText(String text) {
        this.text = text;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public void setCheck(String[] check) {
        checked = new HashMap<String, Boolean>();
        for (String value : check) {
            checked.put(value, Boolean.TRUE);
        }
        this.check = check;
    }

    // Helpers ------------------------------------------------------------------------------------

    public Map<String, Boolean> getChecked() {
        return checked;
    }

    public Map<String, String> getErrors() {
        return errors;
    }

    public Map<String, String> getMessages() {
        return messages;
    }

    public void setError(String fieldName, String message) {
        errors.put(fieldName, message);
    }

    public void setMessage(String fieldName, String message) {
        messages.put(fieldName, message);
    }

    public boolean hasErrors() {
        return errors.size() > 0;
    }
    
    public ArrayList<Paket> readCSV() {
		String csvFile;
                csvFile = WekaClass.pathOutput;
		BufferedReader br = null;
		String line = "";
		ArrayList<Paket> res=new ArrayList<>();
		try {
                        System.out.println("Perint");
			br = new BufferedReader(new FileReader(csvFile));
			while ((line = br.readLine()) != null) {
				// use comma as separator
                                System.out.println(line);
				String[] tp = line.split(",");
				Paket t=new Paket();
				t.id=tp[0];
				t.predicted=tp[2];
				t.actual=tp[1];
				res.add(t);
			}
			return res;
		} catch (Exception e) {
			e.printStackTrace();
			Paket t=new Paket();
			t.id="INVALID_ID";
			t.predicted="Failed to load output.csv";
			t.actual="Failed to load output.csv";
			res.add(t);
			return res;
		}finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
