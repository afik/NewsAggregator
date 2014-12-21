package mypackage;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.WekaClass;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import weka.core.Instances;

public class MyServlet extends HttpServlet {

    /**
	 * 
	 */
	private static final long serialVersionUID = -2117034443538328201L;
	// Init ---------------------------------------------------------------------------------------

    private File uploadFilePath;

    // Actions ------------------------------------------------------------------------------------

    public void init() throws ServletException {
        // Configure uploadFilePath.
        String uploadFilePathParam = getServletConfig().getInitParameter("uploadFilePath");
        if (uploadFilePathParam == null) {
            throw new ServletException("MyServlet 'uploadFilePath' is not configured.");
        }
        //uploadFilePath = new File(uploadFilePathParam);
        uploadFilePath = new File(getServletContext().getRealPath("")
        						+ File.separator + uploadFilePathParam);
        if (!uploadFilePath.exists()) {
            throw new ServletException("MyServlet 'uploadFilePath' does not exist.");
        }
        if (!uploadFilePath.isDirectory()) {
            throw new ServletException("MyServlet 'uploadFilePath' is not a directory.");
        }
        if (!uploadFilePath.canWrite()) {
            throw new ServletException("MyServlet 'uploadFilePath' is not writeable.");
        }
        WekaClass.setPathUtama(getServletContext().getRealPath("") + File.separator + uploadFilePathParam+File.separator);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException
    {
        // Do nothing, just show the form.
        forward("index.jsp",request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException
    {
    	String tipe = request.getParameter("tipe");
		String dest="index.jsp";
        MyForm myForm = new MyForm();
		if (tipe.equals("Input CSV")){
			processCSV(request, myForm);
			dest="outputFile.jsp";
		} else if (tipe.equals("Input Text")){
			processText(request, myForm);
			dest="index.jsp";
		} else if (tipe.equals("Rebuild")){
			rebuild(request, myForm);
			dest="rebuild.jsp";
		}
		request.setAttribute("myForm", myForm);
		forward(dest,request,response);
    }

    private void processText(HttpServletRequest request, MyForm myForm) {
		String text = request.getParameter("text");
	    if (isEmpty(text)) {
	        // No text entered.
	        myForm.setError("text", "Please enter some text.");
	    }
		if (!myForm.hasErrors()) {
			try {
				WekaClass weka = new WekaClass();
				Instances inst;
				inst = weka.makeInstances(text);
				Instances dataTrain;
				dataTrain = weka.loadData(WekaClass.pathTrain);
				String hsl=weka.classifyIns(inst,dataTrain);
				myForm.setMessage("out", hsl);
			} catch (Exception e) {
				e.printStackTrace();
		        myForm.setError("text", "Failed to load Data Train.");
			}
		}
    }
	private void processCSV(HttpServletRequest request, MyForm myForm) {
		// Validate file.
        Object fileObject = request.getAttribute("file");
        if (fileObject == null) {
            // No file uploaded.
            myForm.setError("file", "Please select file to upload.");
        } else if (fileObject instanceof FileUploadException) {
            // File upload is failed.
            FileUploadException fileUploadException = (FileUploadException) fileObject;
            myForm.setError("file", fileUploadException.getMessage());
        }
		// If there are no errors, proceed with writing file.
        if (!myForm.hasErrors()) {
            FileItem fileItem = (FileItem) fileObject;
			String fileName = "input.csv";
			try {
                File file = new File(uploadFilePath, fileName);
                System.out.println(file.getAbsolutePath());
                fileItem.write(file);
                System.out.println("=========");
                System.out.println(file.exists()?"adad":"tidak");
		// Set the file in form so that it can be provided for download.
                myForm.setFile(file);
            } catch (Exception e) {
                // Can be thrown by uniqueFile() and FileItem#write().
                myForm.setError("file", e.getMessage());
                e.printStackTrace();
            }
        }
        // If there are no errors after writing file, proceed with showing messages.
        if (!myForm.hasErrors()) {
            try {
                myForm.setMessage("file", "File succesfully uploaded.");
                WekaClass weka = new WekaClass();
                Instances dataTest;
                dataTest = weka.loadData(WekaClass.pathUtama+"input.csv");
                Instances dataTrain = weka.loadData(WekaClass.pathTrain);
                ArrayList<Integer> idxWrong = new ArrayList<Integer>();
                idxWrong = weka.classifyCSV(dataTest, dataTrain);
                String msg=new String();
                Instances temp = weka.RemoveAttribute(dataTest);
                temp = weka.Preproccess(temp);
                //msg+=100-(((double)idxWrong.size()/(double)temp.numInstances())*100);
                msg=String.valueOf(new DecimalFormat("0.##").format(100-(((double)idxWrong.size()/(double)temp.numInstances())*100)));
                myForm.setMessage("out",msg);
                String idxsalah=new String();
                for (int i=0;i<idxWrong.size();i++){
                        if (i!=0)idxsalah+=",";
                        idxsalah+=idxWrong.get(i);
                }
                //idxsalah=idxWrong.toString();
                myForm.setMessage("idxsalah",idxsalah);
            } catch (Exception e) {
                e.printStackTrace();
                myForm.setError("error", "Build Model Failed.");
            }
        }
	}
	private void rebuild(HttpServletRequest request, MyForm myForm) {
		WekaClass weka = new WekaClass();
		Instances toCSV=null;
		ArrayList<Integer> idxWrong=null;
		//validate file soal CSV ada/tidak
		File file = new File(WekaClass.pathTrain);
    	if (!file.exists()) {
			myForm.setError("error", "Training Set File does not exist.");
		} else {
			try {
				toCSV = weka.RemoveAttribute(weka.loadData(WekaClass.pathUtama+"input.csv"));
			} catch (Exception e) {
				e.printStackTrace();
				myForm.setError("error", "Training Set Failed to Load.");
			}
		}
		//validate ada parameter idxsalah
		String tmp=request.getParameter("idxsalah");
        if (isEmpty(tmp)) {
            myForm.setError("error", "Rebuild Data is not created yet.");
        } else {
			idxWrong = new ArrayList<Integer>();
			for (String s:tmp.split(",")){
				idxWrong.add(Integer.parseInt(s));
			}
		}
		if (!myForm.hasErrors()) {
			ArrayList<String> NewTrainSt;
			try {
				NewTrainSt = weka.getNewTrain(idxWrong, toCSV);
				weka.addToFile(NewTrainSt);
				Instances newTrain = weka.loadData(WekaClass.pathTrain);
				weka.buildModel(newTrain);
				myForm.setMessage("out", "Rebuild Model Success.");
			} catch (Exception e) {
				e.printStackTrace();
	            myForm.setError("error", "Rebuild Model Failed.");
			}
		}
	}

    private void forward(String dest, HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException
    {
        request.getRequestDispatcher(dest).forward(request, response);
    }

    // Utilities (should be refactored to public utility classes) ---------------------------------

    /**
     * Check if the given object is empty. Returns true if the object is null, or if it is an
     * instance of String and its trimmed length is zero, or if it is an instance of an ordinary
     * array and its length is zero, or if it is an instance of Collection and its size is zero,
     * or if it is an instance of Map and its size is zero, or if its String representation is
     * null or the trimmed length of its String representation is zero.
     * @param value The object to be determined on emptiness.
     * @return True if the given object value is empty.
     */
    public static boolean isEmpty(Object value) {
        if (value == null) {
            return true;
        } else if (value instanceof String) {
            return ((String) value).trim().length() == 0;
        } else if (value instanceof Object[]) {
            return ((Object[]) value).length == 0;
        } else if (value instanceof Collection<?>) {
            return ((Collection<?>) value).size() == 0;
        } else if (value instanceof Map<?, ?>) {
            return ((Map<?, ?>) value).size() == 0;
        } else {
            return value.toString() == null || value.toString().trim().length() == 0;
        }
    }

}