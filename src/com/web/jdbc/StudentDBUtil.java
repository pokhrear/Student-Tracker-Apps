package com.web.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

public class StudentDBUtil {
	private DataSource dataSource;

	// constructor
	public StudentDBUtil(DataSource theDataSource) {
		dataSource = theDataSource;
	}

	public List<Student> getStudents() throws Exception {
		List<Student> students = new ArrayList<>();

		Connection myCon = null;
		Statement myStmt = null;
		ResultSet myRs = null;
		try {
			// get a connection
			myCon = dataSource.getConnection();

			// create sql statement

			String sql = "select * from student order by last_name";
			myStmt = myCon.createStatement();

			// execute query
			myRs = myStmt.executeQuery(sql);

			// process result set
			while (myRs.next()) {
				// retrieve data from result set row
				int id = myRs.getInt("id");
				String fname = myRs.getString("first_name");
				String lname = myRs.getString("last_name");
				String email = myRs.getString("email");

				// create new student object
				Student tempStudent = new Student(id, fname, lname, email);

				// add it to the list of student
				students.add(tempStudent);
			}
			return students;

		}

		finally {
			// close JDBC object
			close(myCon, myStmt, myRs);
		}

	}

	private void close(Connection myCon, Statement myStmt, ResultSet myRs) {
		try {
			if (myRs != null) {
				myRs.close();
			}
			if (myStmt != null) {
				myStmt.close();
			}
			if (myCon != null) {
				myCon.close();
			}

		} catch (Exception ex) {
			ex.printStackTrace();

		}

	}

	public void addStudent(Student theStudent) throws Exception {

		Connection myconn = null;
		PreparedStatement mystmt = null;

		try {
			// get our connection
			myconn = dataSource.getConnection();

			// create sql for insert
			String sql = "insert into student" + "(first_name, last_name, email)" + "values(?, ? ,?)";

			mystmt = myconn.prepareStatement(sql);
			// set the param values for the student
			mystmt.setString(1, theStudent.getFname());
			mystmt.setString(2, theStudent.getLname());
			mystmt.setString(3, theStudent.getEmail());

			// execute sql insert
			mystmt.execute();
		} finally {
			// clean up JDBC objects
			close(myconn, mystmt, null);
		}
	}

	public Student getStudent(String theStudentId) throws Exception {

		Student theStudent = null;
		Connection myconn = null;
		PreparedStatement mystmt = null;
		ResultSet myrs = null;
		int studentId;

		try {
			// convert student id into int
			studentId = Integer.parseInt(theStudentId);

			// get connection to database
			myconn = dataSource.getConnection();

			// create sql to get selected student
			String sql = "select * from student"
						+"where id =?";

			// create prepared statment
			mystmt = myconn.prepareStatement(sql);

			// set params
			mystmt.setInt(1, studentId);

			// set params
			myrs = mystmt.executeQuery();

			// retrive data from result set row
			if (myrs.next()) {
				String fname = myrs.getString("first_name");
				String lname = myrs.getString("last_name");
				String email = myrs.getString("email");

				// use the studentId during construction

				theStudent = new Student(studentId, fname, lname, email);
			} else {
				throw new Exception("Could not find student id: " + studentId);
			}

			return theStudent;
		} finally {
			// close the connection
			close(myconn, mystmt, myrs);
		}

	}

	public void updateStudent(Student theStudent) throws Exception {

		Connection myconn = null;
		PreparedStatement mystmt = null;
		try {
			// get db connection

			myconn = dataSource.getConnection();
			// create SQL update statement
			String sql = "update student"
						+"set first_name=?, last_name =?, email =?"
						+"where id=? ";
			// prepare statement
			mystmt = myconn.prepareStatement(sql);
			// prepare params
			mystmt.setString(1, theStudent.getFname());
			mystmt.setString(2, theStudent.getLname());
			mystmt.setString(3, theStudent.getEmail());
			mystmt.setInt(4, theStudent.getId());

			// execute SQL statement
			mystmt.execute();
		} 
		finally {
			// clean up the connection
			close(myconn, mystmt, null);
		}

	}

	public void deleteStudent(String theStudentId) throws Exception{
		Connection myconn =null;
		PreparedStatement mystmt=null;
		
		try {
			//converting student id to int
			int studentId = Integer.parseInt(theStudentId);
			
			
			//get connection
			myconn = dataSource.getConnection();
			
			//create sql to delete student
			String sql = "delete from student where id=?";
			//prepare statement
			mystmt = myconn.prepareStatement(sql);
			
			//set params
			mystmt.setInt(1,  studentId);
			
			//execute sql statement
			mystmt.execute();
			
			
		}
		finally {
			
			//clean up code
			close(myconn, mystmt, null);
			
		}

		
	}

}
