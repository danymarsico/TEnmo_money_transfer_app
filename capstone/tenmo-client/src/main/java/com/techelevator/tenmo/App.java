package com.techelevator.tenmo;

import com.techelevator.tenmo.model.AuthenticatedUser;
import com.techelevator.tenmo.model.TransferHistory;
import com.techelevator.tenmo.model.UserCredentials;
import com.techelevator.tenmo.services.AuthenticationService;
import com.techelevator.tenmo.services.BalanceTransferService;
import com.techelevator.tenmo.services.ConsoleService;
import com.techelevator.tenmo.services.TransferHistoryService;

import java.math.BigDecimal;
import java.util.List;

public class App {

    private static final String API_BASE_URL = "http://localhost:8080/";

    private final ConsoleService consoleService = new ConsoleService();
    private final AuthenticationService authenticationService = new AuthenticationService(API_BASE_URL);

    private AuthenticatedUser currentUser;
    private final BalanceTransferService balanceTransferService = new BalanceTransferService();

    private final TransferHistoryService transferHistoryService = new TransferHistoryService();

    public static void main(String[] args) {
        App app = new App();
        app.run();
    }

    private void run() {
        consoleService.printGreeting();
        loginMenu();
        if (currentUser != null) {
            mainMenu();
        }
    }
    private void loginMenu() {
        int menuSelection = -1;
        while (menuSelection != 0 && currentUser == null) {
            consoleService.printLoginMenu();
            menuSelection = consoleService.promptForMenuSelection("Please choose an option: ");
            if (menuSelection == 1) {
                handleRegister();
            } else if (menuSelection == 2) {
                handleLogin();
            } else if (menuSelection != 0) {
                System.out.println("Invalid Selection");
                consoleService.pause();
            }
        }
    }

    private void handleRegister() {
        System.out.println("Please register a new user account");
        UserCredentials credentials = consoleService.promptForCredentials();
        if (authenticationService.register(credentials)) {
            System.out.println("Registration successful. You can now login.");
        } else {
            consoleService.printErrorMessage();
        }
    }

    private void handleLogin() {
        UserCredentials credentials = consoleService.promptForCredentials();
        currentUser = authenticationService.login(credentials);
        balanceTransferService.setAuthToken(currentUser.getToken());
        transferHistoryService.setAuthToken(currentUser.getToken());
        if (currentUser == null) {
            consoleService.printErrorMessage();
        }
    }

    private void mainMenu() {
        int menuSelection = -1;
        while (menuSelection != 0) {
            consoleService.printMainMenu();
            menuSelection = consoleService.promptForMenuSelection("Please choose an option: ");
            if (menuSelection == 1) {
                viewCurrentBalance();
            } else if (menuSelection == 2) {
                viewTransferHistory();
            } else if (menuSelection == 3) {
                viewPendingRequests();
            } else if (menuSelection == 4) {
                sendBucks();
            } else if (menuSelection == 5) {
                requestBucks();
            } else if (menuSelection == 0) {
                continue;
            } else {
                System.out.println("Invalid Selection");
            }
            consoleService.pause();
        }
    }

	private void viewCurrentBalance() {
        BigDecimal balance = balanceTransferService.getBalance();
        System.out.println("Your current balance is: " + balance + "$" );
	}

	private void viewTransferHistory() {
        List<TransferHistory> transferHistories = transferHistoryService.viewTransfers();
        System.out.println("-------------------------------------------------------");
        System.out.println("ID          From          To                 Amount");
        System.out.println("-------------------------------------------------------");
        for(TransferHistory th : transferHistories){
            System.out.println(th.toString());
        }
        while(true){
            int ans = consoleService.promptForInt("To view details of a certain transfer, enter the transfer ID, to Exit press 0.");
            if (ans != 0){
                TransferHistory specificTransfer = transferHistoryService.viewTransferById(ans);
                System.out.println(specificTransfer.specificToString());
            }
            else {
                break;
            }
        }
	}

	private void viewPendingRequests() {
		// TODO Auto-generated method stub
		
	}

	private void sendBucks() {
        List<String> usernames = balanceTransferService.getUsernames();
        System.out.println("Here is a list of usernames:");
        for(String username : usernames){
            System.out.println(username);
        }
        String userToSend = null;
        while (!usernames.contains(userToSend)) {
            userToSend = consoleService.promptForString("Please enter the username you want to transfer TE-Bucks to: ");
            if (!usernames.contains(userToSend)){
                System.out.println("Username does not exist.");
            }
        }
        viewCurrentBalance();
        BigDecimal transferAmount = consoleService.promptForBigDecimal("Please enter the amount of money to transfer: ");
        balanceTransferService.transfer(userToSend, transferAmount);
        viewCurrentBalance();
	}

	private void requestBucks() {
		// TODO Auto-generated method stub
		
	}

}
