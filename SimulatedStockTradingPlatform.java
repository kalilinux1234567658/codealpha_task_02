import java.util.*;

public class SimulatedStockTradingPlatform {

    static class Stock {
        String symbol;
        String name;
        double price;

        Stock(String symbol, String name, double price) {
            this.symbol = symbol;
            this.name = name;
            this.price = price;
        }
    }

    static class Holding {
        Stock stock;
        int quantity;

        Holding(Stock stock, int quantity) {
            this.stock = stock;
            this.quantity = quantity;
        }

        double getValue() {
            return stock.price * quantity;
        }
    }

    static class Portfolio {
        Map<String, Holding> holdings = new HashMap<>();
        double cash;

        Portfolio(double initialCash) {
            this.cash = initialCash;
        }

        boolean buyStock(Stock stock, int quantity) {
            double cost = stock.price * quantity;
            if (quantity <= 0) {
                System.out.println("Quantity must be positive.");
                return false;
            }
            if (cost > cash) {
                System.out.println("Insufficient cash to buy " + quantity + " shares of " + stock.symbol);
                return false;
            }
            cash -= cost;
            Holding holding = holdings.get(stock.symbol);
            if (holding != null) {
                holding.quantity += quantity;
            } else {
                holdings.put(stock.symbol, new Holding(stock, quantity));
            }
            System.out.println("Bought " + quantity + " shares of " + stock.symbol + " for $" + String.format("%.2f", cost));
            return true;
        }

        boolean sellStock(Stock stock, int quantity) {
            Holding holding = holdings.get(stock.symbol);
            if (holding == null || holding.quantity < quantity) {
                System.out.println("You do not have enough shares of " + stock.symbol + " to sell.");
                return false;
            }
            if (quantity <= 0) {
                System.out.println("Quantity must be positive.");
                return false;
            }
            double revenue = stock.price * quantity;
            holding.quantity -= quantity;
            if (holding.quantity == 0) {
                holdings.remove(stock.symbol);
            }
            cash += revenue;
            System.out.println("Sold " + quantity + " shares of " + stock.symbol + " for $" + String.format("%.2f", revenue));
            return true;
        }

        void printPortfolio() {
            System.out.println("\nYour Portfolio:");
            if (holdings.isEmpty()) {
                System.out.println("  (No holdings)");
            } else {
                double totalValue = 0.0;
                System.out.printf("%-8s %-20s %-10s %-10s%n", "Symbol", "Name", "Quantity", "Value($)");
                for (Holding holding : holdings.values()) {
                    double value = holding.getValue();
                    totalValue += value;
                    System.out.printf("%-8s %-20s %-10d %-10.2f%n", holding.stock.symbol, holding.stock.name, holding.quantity, value);
                }
                System.out.printf("Total value of holdings: $%.2f%n", totalValue);
            }
            System.out.printf("Available cash: $%.2f%n", cash);
        }
    }

    static class Market {
        Map<String, Stock> stocks = new HashMap<>();
        Random random = new Random();

        Market() {
            // Initialize some stocks with symbols, names and base prices
            stocks.put("AAPL", new Stock("AAPL", "Apple Inc.", 145.0));
            stocks.put("GOOGL", new Stock("GOOGL", "Alphabet Inc.", 2800.0));
            stocks.put("MSFT", new Stock("MSFT", "Microsoft Corp.", 300.0));
            stocks.put("TSLA", new Stock("TSLA", "Tesla Inc.", 700.0));
            stocks.put("AMZN", new Stock("AMZN", "Amazon.com Inc.", 3300.0));
        }

        void simulateMarketMovement() {
            for (Stock stock : stocks.values()) {
                // Simulate price change: +/- up to 5%
                double changePercent = (random.nextDouble() * 10) - 5;
                stock.price += stock.price * changePercent / 100;
                if (stock.price < 1) stock.price = 1; // minimum price safeguard
            }
        }

        void printMarketData() {
            System.out.println("\nCurrent Market Data:");
            System.out.printf("%-8s %-20s %-10s%n", "Symbol", "Name", "Price($)");
            for (Stock stock : stocks.values()) {
                System.out.printf("%-8s %-20s %-10.2f%n", stock.symbol, stock.name, stock.price);
            }
        }

        Stock getStockBySymbol(String symbol) {
            return stocks.get(symbol.toUpperCase());
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Market market = new Market();
        Portfolio portfolio = new Portfolio(10000.0);  // start with $10,000 cash

        System.out.println("Welcome to the Simulated Stock Trading Platform!");
        System.out.println("You start with $10,000 in cash.\n");

        boolean exit = false;

        while (!exit) {
            System.out.println("\nMenu:");
            System.out.println("1. View Market Data");
            System.out.println("2. Buy Stocks");
            System.out.println("3. Sell Stocks");
            System.out.println("4. View Portfolio");
            System.out.println("5. Simulate Market Movement");
            System.out.println("6. Exit");
            System.out.print("Choose an option (1-6): ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    market.printMarketData();
                    break;
                case "2":
                    System.out.print("Enter the stock symbol to buy: ");
                    String buySymbol = scanner.nextLine().trim().toUpperCase();
                    Stock buyStock = market.getStockBySymbol(buySymbol);
                    if (buyStock == null) {
                        System.out.println("Invalid stock symbol.");
                        break;
                    }
                    System.out.print("Enter quantity to buy: ");
                    try {
                        int buyQty = Integer.parseInt(scanner.nextLine().trim());
                        portfolio.buyStock(buyStock, buyQty);
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid quantity.");
                    }
                    break;
                case "3":
                    System.out.print("Enter the stock symbol to sell: ");
                    String sellSymbol = scanner.nextLine().trim().toUpperCase();
                    Stock sellStock = market.getStockBySymbol(sellSymbol);
                    if (sellStock == null) {
                        System.out.println("Invalid stock symbol.");
                        break;
                    }
                    System.out.print("Enter quantity to sell: ");
                    try {
                        int sellQty = Integer.parseInt(scanner.nextLine().trim());
                        portfolio.sellStock(sellStock, sellQty);
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid quantity.");
                    }
                    break;
                case "4":
                    portfolio.printPortfolio();
                    break;
                case "5":
                    market.simulateMarketMovement();
                    System.out.println("Market prices have been updated.");
                    break;
                case "6":
                    exit = true;
                    System.out.println("Thank you for using the Simulated Stock Trading Platform!");
                    break;
                default:
                    System.out.println("Invalid option, please choose 1-6.");
            }
        }
        scanner.close();
    }
}
