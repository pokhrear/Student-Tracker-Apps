package com.web.jdbc;

import java.io.IOException;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

/**
 * Servlet implementation class StudentControllerServlet
 */
@WebServlet("/StudentControllerServlet")
public class StudentControllerServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private StudentDBUtil studentDBUtil;
	@Resource(name = "jdbc/web_student_tracker")
	private DataSource dataSource;

	@Override
	public void init() throws ServletException {
		// TODO Auto-generated method stub
		super.init();

		// create our student db util .. and pass in the conn pool /database
		try {
			studentDBUtil = new StudentDBUtil(dataSource);
		} catch (Exception ex) {
			throw new ServletException(ex);
		}
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		try {

			// read the command parameter

			String theCommand = request.getParameter("command");

			// if the command is missing , then default to listing student

			if (theCommand == null) {
				theCommand = "List";
			}

			// route the appropriate method

			switch (theCommand) {
			case "List":
				listStudents(request, response);
				break;

			case "ADD":
				addStudent(request, response);
				break;
				
			case "LOAD":
				loadStudent(request, response);
				break;
				
				
			case "UPDATE":
				updateStudent(request, response);
				break;
				
			case "DELETE":
				deleteStudent(request, response);
				break;

			default:
				listStudents(request, response);
			}

			// list the student ... in the mvc fashion
			listStudents(request, response);
		} catch (Exception ex) {
			throw new ServletException(ex);
		}

	}

	private void deleteStudent(HttpServletRequest request, HttpServletResponse response) throws Exception{
		
		//read student is from form data
		String theStudentId = request.getParameter("studentId");
		
		//delete the student from the database
		studentDBUtil.deleteStudent(theStudentId);
		
		//send them back to list student page
		listStudents(request, response);

	}

	private void updateStudent(HttpServletRequest request, HttpServletResponse response) throws Exception{
		
		//read student info from the form data
		int id = Integer.parseInt(request.getParameter("studentId"));
		String fname = request.getParameter("fname");
		String lname = request.getParameter("lname");
		String email = request.getParameter("email");
		
		//create a new student object
		Student theStudent = new Student(id, fname,lname, email);
		
		
		//perform update on database
		studentDBUtil.updateStudent(theStudent);
		
		//send them back to the list student page
		listStudents(request, response);
	}

	private void loadStudent(HttpServletRequest request, HttpServletResponse response) throws Exception{
		//read student id from form data
		String theStudentId = request.getParameter("studentId");
		
		//get student from database(DB util)
		Student theStudent = studentDBUtil.getStudent(theStudentId);
		
		//place student in the request attribute
		request.setAttribute("THE_STUDENT",theStudent);
		
		//send to jsp page: update-student-form.jsp
		RequestDispatcher dispatcher = request.getRequestDispatcher("/update-student-form.jsp");
		dispatcher.forward(request, response);
		
	}

	private void addStudent(HttpServletRequest request, HttpServletResponse response) throws Exception {

		// read student info from form data
		String fName = request.getParameter("fname");
		String lName = request.getParameter("lname");
		String email = request.getParameter("email");

		// create a new student object
		Student theStudent = new Student(fName, lName, email);

		// add the student to the database
		studentDBUtil.addStudent(theStudent);

		// send back to the main page (the student List)
		listStudents(request, response);

	}

	private void listStudents(HttpServletRequest request, HttpServletResponse response)

			throws Exception {

		// get student from DB util

		List<Student> students = studentDBUtil.getStudents();

		// add student to the request
		request.setAttribute("STUDENT_LIST", students);

		// send to JSP page (View)
		RequestDispatcher dispatcher = request.getRequestDispatcher("/list_student.jsp");
		dispatcher.forward(request, response);

	}

}
