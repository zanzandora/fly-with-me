/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package com.flywithme.controller.admin;

import com.flywithme.dao.AirportDAO;
import com.flywithme.model.Airport;
import com.flywithme.model.DBconnect;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author pc
 */
@WebServlet(name = "AdminAirportManagement", urlPatterns = {"/AdminAirportManagement"})
public class AdminAirportManagement extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet AdminAMManagement</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet AdminAMManagement at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            String action = request.getParameter("X-Action");
            if (action == null) {
                displayAirport(request, response);
                return;
            }
            switch (action) {
                case "create" ->
                    createAirport(request, response);
                case "edit" ->
                    editAirport(request, response);
                case "delete" ->
                    deleteAirport(request, response);
                default ->
                    displayAirport(request, response);
            }
        } catch (SQLException ex) {
            Logger.getLogger(AdminAirportManagement.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);

    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

    private void displayAirport(HttpServletRequest request, HttpServletResponse response) throws SQLException, IOException {
        AirportDAO airportDAO = new AirportDAO();
        List<Airport> airports = new ArrayList<>();
        HttpSession session = request.getSession();
        try {
            // Lấy danh sách khách hàng từ DAO
            airports = airportDAO.getAllAirports();
        } catch (SQLException e) {
            // Xử lý lỗi nếu có
            e.printStackTrace();
        }
        session.setAttribute("airports", airports);
        response.sendRedirect(request.getContextPath() + "/admin/admin_page.jsp");
    }

    private void createAirport(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession();

        String masanbay = "AP" + UUID.randomUUID().toString().substring(0, 5);
        String matkhausanbay = request.getParameter("airportPassword");
        String tensanbay = request.getParameter("airportName");
        String quocsgia = request.getParameter("airportCountry");

        // Khởi tạo đối tượng CustomerDAO
        AirportDAO airportDAO = new AirportDAO();
        String message;

        try {
            // Gọi phương thức DAO để thêm khách hàng
            boolean isCreated = airportDAO.createAirport(masanbay, matkhausanbay, tensanbay, quocsgia);
            if (isCreated) {
                message = "Airport created successfully!";

                // Thêm hãng hàng không vào session
                Airport newAirport = new Airport(masanbay, matkhausanbay, tensanbay, quocsgia);
                List<Airport> airports = (List<Airport>) session.getAttribute("airports");
                if (airports == null) {
                    airports = new ArrayList<>();
                }
                airports.add(newAirport);  // Thêm airport mới vào danh sách hiện tại
                session.setAttribute("airports", airports);  // Cập nhật session

                response.sendRedirect(request.getContextPath() + "/admin/admin_page.jsp");
            } else {
                message = "Failed to create customers!";
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            message = "An error occurred: " + ex.getMessage();
        }

        session.setAttribute("message", message);
    }

    private void editAirport(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession();

        String masanbay = request.getParameter("airportID");
        String matkhausanbay = request.getParameter("airportPassword");
        String tensanbay = request.getParameter("airportName");
        String quocsgia = request.getParameter("airportCountry");

        // Khởi tạo đối tượng AirlineDAO
        AirportDAO airportDAO = new AirportDAO();
        String message;

        try {

            // Gọi phương thức DAO để cập nhật hãng hàng không
            boolean isUpdated = airportDAO.updateAirport(masanbay, matkhausanbay, tensanbay, quocsgia);
            if (isUpdated) {
                message = "Customer updated successfully!";

                // Thêm hãng hàng không vào session
                Airport updatedAirport = new Airport(masanbay, matkhausanbay, tensanbay, quocsgia);
                List<Airport> airports = (List<Airport>) session.getAttribute("airports");
                
                airports = airportDAO.updateAndGetAirportsFromSession(masanbay, matkhausanbay, tensanbay, quocsgia, airports);


                session.setAttribute("airports", airports);  // Cập nhật session

                // Chuyển hướng về trang admin_page.jsp
                response.sendRedirect(request.getContextPath() + "/admin/admin_page.jsp");
            } else {
                message = "Failed to update airline!";
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            message = "An error occurred: " + ex.getMessage();
        }
    }

    private void deleteAirport(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String masanbay = request.getParameter("id");
        AirportDAO airportDAO = new AirportDAO();

        HttpSession session = request.getSession();

        String message;
        try {
            boolean isDeleted = airportDAO.deleteAirport(masanbay);
            if (isDeleted) {
                message = "Airport deleted successfully!";

                // Cập nhật lại danh sách airlines trong session
                List<Airport> airports = (List<Airport>) session.getAttribute("airports");
                if (airports != null) {
                    airports.removeIf(airport -> airport.getMaSanBay().equals(masanbay));
                    session.setAttribute("airports", airports);
                }
            } else {
                message = "Failed to delete airports!";

            }
        } catch (Exception e) {
            e.printStackTrace();
            message = "An error occurred: " + e.getMessage();
        }
        session.setAttribute("message", message);
        response.sendRedirect(request.getContextPath() + "/admin/admin_page.jsp");
    }

}