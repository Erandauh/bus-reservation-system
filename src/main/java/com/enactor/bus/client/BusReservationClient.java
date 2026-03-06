package com.enactor.bus.client;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Scanner;

public class BusReservationClient {
    private static final String BASE_URL = "http://localhost:8080/bus-reservation-system/api/v1";
    private static final HttpClient client = HttpClient.newHttpClient();

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("=== Enactor Bus Reservation System Client ===");

        label:
        while (true) {
            System.out.println("\nSelect an option:");
            System.out.println("1. Check Availability (GET)");
            System.out.println("2. Make a Reservation (POST)");
            System.out.println("3. Exit");
            System.out.print("> ");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    handleAvailability(scanner);
                    break;
                case "2":
                    handleReservation(scanner);
                    break;
                case "3":
                    System.out.println("Exiting client...");
                    break label;
                default:
                    System.out.println("Invalid choice. Try again.");
                    break;
            }
        }
    }

    private static void handleAvailability(Scanner scanner) {
        System.out.print("Enter Origin: ");
        String origin = scanner.nextLine();
        System.out.print("Enter Destination: ");
        String dest = scanner.nextLine();
        System.out.print("Enter Number of Passengers: ");
        String passengers = scanner.nextLine();

        String url = String.format("%s/availability?origin=%s&destination=%s&passengers=%s",
                BASE_URL, origin, dest, passengers);

        sendRequest(url, "GET", null);
    }

    private static void handleReservation(Scanner scanner) {
        System.out.print("Enter Origin: ");
        String origin = scanner.nextLine();
        System.out.print("Enter Destination: ");
        String dest = scanner.nextLine();
        System.out.print("Enter Number of Passengers: ");
        int passengers = Integer.parseInt(scanner.nextLine());
        System.out.print("Enter Price to Pay: ");
        double price = Double.parseDouble(scanner.nextLine());

        // Constructing the JSON body manually to avoid needing a heavy JSON library in the client
        String jsonBody = String.format(
                "{\"origin\":\"%s\", \"destination\":\"%s\", \"passengers\":%d, \"price\":%.2f}",
                origin, dest, passengers, price
        );

        sendRequest(BASE_URL + "/reserve", "POST", jsonBody);
    }

    private static void sendRequest(String url, String method, String body) {
        try {
            HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                    .uri(URI.create(url.replace(" ", "%20"))) // Handle spaces in URL
                    .header("Content-Type", "application/json");

            if (method.equals("POST")) {
                requestBuilder.POST(HttpRequest.BodyPublishers.ofString(body));
            } else {
                requestBuilder.GET();
            }

            HttpResponse<String> response = client.send(requestBuilder.build(),
                    HttpResponse.BodyHandlers.ofString());

            System.out.println("\n--- Server Response ---");
            System.out.println("Status: " + response.statusCode());
            System.out.println("Body: " + response.body());
            System.out.println("-----------------------");
        } catch (Exception e) {
            System.err.println("Error: Could not connect to Tomcat. Is it running on port 8080?");
        }
    }
}