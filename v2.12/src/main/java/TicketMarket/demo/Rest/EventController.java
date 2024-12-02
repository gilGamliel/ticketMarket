package TicketMarket.demo.Rest;

import TicketMarket.demo.DAO.EventRepository;
import TicketMarket.demo.DAO.TicketRepository;
import TicketMarket.demo.DAO.UserRepository;
import TicketMarket.demo.Entity.Event;
import TicketMarket.demo.Entity.Ticket;
import TicketMarket.demo.Entity.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.time.LocalDateTime;
import java.util.List;

@RequestMapping("/event")
@Controller
public class EventController {
    private EventRepository eventRepository ;
    private TicketRepository ticketRepository;
    private UserRepository userRepository;
    @Autowired
    public EventController(EventRepository eventRepository, TicketRepository ticketRepository , UserRepository userRepository) {
        this.eventRepository = eventRepository;
        this.ticketRepository = ticketRepository;
        this.userRepository  = userRepository ;
    }


    @GetMapping("/createEvent")
    public String createEvent(HttpSession session){
        if (session.getAttribute("loggedInUser") == null ) return "redirect:/signin";
        return "createEventForm";
    }
    @RequestMapping(value = "/processEvent" , method = RequestMethod.POST)
    public String processEvent(HttpServletRequest http ,  HttpSession session , Model model){
        if (session.getAttribute("loggedInUser") == null ) return "redirect:/signin";
        String name = http.getParameter("event_name");
        LocalDateTime event_time = LocalDateTime.parse(http.getParameter("event_date"));
        String eventLoc = http.getParameter("eventLoc");
        String event_desc = http.getParameter("event_desc");
        String tag = http.getParameter("tag");
        User temp = (User) session.getAttribute("loggedInUser");
        if (eventRepository.isEventExist(name))
        {
            model.addAttribute("error" , "Event name alreday exist! ");
            return "createEventForm";
        }
        eventRepository.save(new Event(name , event_time , eventLoc , event_desc ,temp.getUser_name() , tag));
        return "eventCreatedSuccess";
    }
    @RequestMapping("/success_event_created")
    public String success_event_created(HttpSession session){
        if (session.getAttribute("loggedInUser") == null ) return "redirect:/signin";
        return "eventCreatedSuccess";
    }

    @GetMapping("/{id}")
    public String eventPage(@PathVariable int id ,HttpSession session , Model model){
        if (session.getAttribute("loggedInUser") == null ) return "redirect:/signin";
        Event event = eventRepository.findById(id).orElseThrow(() -> new RuntimeException("Event not found"));
        model.addAttribute("event", event);

        return "eventPage";
    }
    @GetMapping("/{id}/Tickets")
    public String eventTickets(@PathVariable int id ,  HttpServletRequest http , HttpSession session , Model model){
        if (session.getAttribute("loggedInUser") == null ) return "redirect:/signin";
        Event event = eventRepository.findById(id).orElseThrow(() -> new RuntimeException("Event not found"));
        model.addAttribute("event", event);
        List<Ticket> ticketList = ticketRepository.availableTicketsByEventId(id);
        model.addAttribute("ticketsList" , ticketList);
        model.addAttribute("eventAvailableTicketsCount" ,eventRepository.amountOfAvilableTickets(id) );
        model.addAttribute("eventSoldTicketsCount" ,eventRepository.amountOfSoldTickets(id) );
        model.addAttribute("eventLookingForTicketsCount" ,eventRepository.amountOfLookingForTickets(id) );
        return "eventTicketsPage";
    }
    @GetMapping("/{id}/Tickets/newTicket")
    public String newEventTicket(@PathVariable int id  , HttpSession session , Model model){
        Event event = eventRepository.findById(id).orElseThrow(() -> new RuntimeException("Event not found"));
        model.addAttribute("event" , event);
        return "newEventTicketForm";
    }
    @RequestMapping(value = "/{id}/Tickets/processNewTicket", method = RequestMethod.POST)
    public String processNewEventTicket(@PathVariable int id, HttpServletRequest http, HttpSession session, Model model) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) {
            model.addAttribute("error", "No User Found!");
            return "redirect:/signin";
        }
        Event event = eventRepository.findById(id).orElseThrow(() -> new RuntimeException("Event not found"));
        model.addAttribute("event", event);

        // Get and validate the price
        String priceStr = http.getParameter("price");
        int price;
        try {
            price = Integer.parseInt(priceStr);
            if (price <= 0) {
                model.addAttribute("error", "Price must be a positive integer.");
                return "newEventTicketForm";
            }
        } catch (NumberFormatException e) {
            model.addAttribute("error", "Invalid price value. Please enter a valid number.");
            return "newEventTicketForm";
        }

        // Get other parameters
        String description = http.getParameter("description");
        String serialKey = http.getParameter("serialKey");

        // Validate serial key
        if (!verifyTicket(serialKey)) {
            model.addAttribute("error", "Serial key is not valid.");
            return "newEventTicketForm";
        }
        Ticket temp = new Ticket(id , user.getUser_id(),  price , description , "available" , serialKey);
        // Save the new ticket
        ticketRepository.save(temp);

        // Redirect to success page
        return "ticketCreateSuccess";
    }
    public static boolean verifyTicket(String serialKey){
        return true ;
    }
    @GetMapping("/{eventId}/Tickets/{TicketId}")
    public String buyEventTicket(@PathVariable int eventId , @PathVariable int TicketId , HttpSession session , HttpServletRequest http , Model model){
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) {
            model.addAttribute("error", "No User Found!");
            return "redirect:/signin";
        }
        Ticket ticket = ticketRepository.findById(TicketId)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));
        model.addAttribute("ticket" , ticket);
        model.addAttribute("event" , event);
        return "buyTicketPage";
    }
    @RequestMapping("/{eventId}/Tickets/{TicketId}/processBuyingTicket")
    public String processBuyingTicket(@PathVariable int eventId  , @PathVariable int TicketId, HttpSession session , HttpServletRequest http , Model model){
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) {
            model.addAttribute("error", "No User Found!");
            return "redirect:/signin";
        }
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));
        Ticket ticket = ticketRepository.findById(TicketId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        User seller = userRepository.findById(ticket.getSeller_id())
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (ticket == null) {
            model.addAttribute("error" , "Not Ticket Found");
            return "redirect:/signin";
        }
        if (user.getBalance() < ticket.getPrice()) {
            model.addAttribute("error", "Insufficient balance to purchase the ticket.");
            model.addAttribute("ticket", ticket);
            model.addAttribute("event", event);
            return "buyTicketPage"; // Redirect back to the ticket page with the error
        }
        user.setBalance(user.getBalance() - ticket.getPrice());
        seller.setBalance(seller.getBalance() + ticket.getPrice());
        ticket.setStatus("sold");
        ticketRepository.save(ticket);
        userRepository.save(user);
        userRepository.save(seller);
        model.addAttribute("ticket", ticket);
        model.addAttribute("event", event);

        return "successBuyingTicketPage";

    }



}

