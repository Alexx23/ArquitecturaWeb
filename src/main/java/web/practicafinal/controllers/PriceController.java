package web.practicafinal.controllers;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import web.practicafinal.enums.PriceEnum;
import web.practicafinal.utils.Response;

@WebServlet("/price/*")
public class PriceController extends HttpServlet {

    public PriceController() {
        super();
    }
    
    /*
    /price -> Obtener tabla de precios
    */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        //////////////////////
        // RUTA PÚBLICA
        //////////////////////
        
        List<PriceContainer> prices = new ArrayList<>();
        
        PriceContainer pc = new PriceContainer(PriceEnum.NORMAL.getId(), PriceEnum.NORMAL.name(), PriceEnum.NORMAL.getPrice());
        prices.add(pc);
        
        Response.outputData(response, 200, prices);
        return;
        
    }
    
    
    private class PriceContainer {
        private int id;
        private String name;
        private int amount;
        
        public PriceContainer(int id, String name, int amount) {
            this.id = id;
            this.name = name;
            this.amount = amount;
        }

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public int getAmount() {
            return amount;
        }


    }
 
   
}