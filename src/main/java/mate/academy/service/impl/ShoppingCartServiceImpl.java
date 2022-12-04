package mate.academy.service.impl;

import java.util.ArrayList;
import java.util.Optional;
import javax.persistence.EntityNotFoundException;
import mate.academy.dao.ShoppingCartDao;
import mate.academy.dao.TicketDao;
import mate.academy.exception.DataProcessingException;
import mate.academy.lib.Inject;
import mate.academy.lib.Service;
import mate.academy.model.MovieSession;
import mate.academy.model.ShoppingCart;
import mate.academy.model.Ticket;
import mate.academy.model.User;
import mate.academy.service.ShoppingCartService;

@Service
public class ShoppingCartServiceImpl implements ShoppingCartService {
    @Inject
    private TicketDao ticketDao;

    @Inject
    private ShoppingCartDao shoppingCartDao;

    @Override
    public void addSession(MovieSession movieSession, User user) {
        Optional<ShoppingCart> shoppingCart = shoppingCartDao.getByUser(user);
        if (shoppingCart.isPresent()) {
            Ticket ticket = new Ticket();
            ticket.setMovieSession(movieSession);
            ticket.setUser(user);
            ticketDao.add(ticket);
            shoppingCart.get().getTickets().add(ticket);
            shoppingCartDao.update(shoppingCart.get());
        } else {
            throw new DataProcessingException("Can't add movie session, shopping cart don't exist");
        }

    }

    @Override
    public ShoppingCart getByUser(User user) {
        return shoppingCartDao.getByUser(user).orElseThrow(() ->
                new EntityNotFoundException("Can't get shopping cart by user " + user));
    }

    @Override
    public void registerNewShoppingCart(User user) {
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUser(user);
        shoppingCartDao.add(shoppingCart);
    }

    @Override
    public void clear(ShoppingCart shoppingCart) {
        shoppingCart.setTickets(new ArrayList<>());
        shoppingCartDao.update(shoppingCart);
    }
}