package com.example;
import java.util.Random;
import java.util.Scanner;

public class Hammurabi {

    //random number generator
    Random rand = new Random();

    // scanner for user input
    Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        
        new Hammurabi().playGame(); //How we start the game; 
                                    //new Hammurabi() creates instance of Hammurabi class
                                    //.playGame() calls the play game method 
    }

    // Game logic here
    void playGame() {

        //declare local variables -> store the state of the game (only exist while the game is running)
        int year = 1; //current year in the game
        int acresOwned = 1000; //acres of land owned
        int landPrice = 19; //price of land per acre
        int population = 100; //current population
        int bushelsInStorage = 2800; //bushels of grain in storage

        //variables to store what happened previous year ***
        int prevPeopleStarved = 0; // number of people who starved last year
        int prevNewImmigrants = 5; // number of new immigrants last year
        int prevHarvestedBushels=3000; // total bushels harvested last year
        int prevYieldPerAcre = 3; //yield per acre last year
        int prevRatsAte=200; //amount of grain eaten by rats last year


        int totalStarvedOverTerm = 0; //total number of people who starved over the game term (accumulates)

        //check if the game is over
        boolean gameOver=false;

        //Print the welcome message
        System.out.println("Welcome to Hammurabi!");
        System.out.println("Congratulations, you are the newest ruler of ancient Sumer, elected for a ten year term of office.");
        System.out.println("Your duties are to dispense food, direct farming, and buy and sell land as needed to support your people.");
        System.out.println("Watch out for rat infestations and the plague! Grain is the general currency, measured in bushels.");
        System.out.println("\n--- Game Start ! ---");




        // ------ Game loop ------
        // The game continues until the player has played for 10 years
        while (year <= 10) {

            //Print the summary of the previous year
            printSummary(year, prevPeopleStarved, prevNewImmigrants, population, prevHarvestedBushels, prevYieldPerAcre, prevRatsAte, bushelsInStorage, acresOwned, landPrice);

            // Implement game logic here -> asking for user input, calculating deaths, etc.

            //initiate variables for user input
            int acresToBuy = 0;
            int acresToSell = 0;
            int bushelsToFeed = 0;
            int acresToPlant = 0;


            //----- Players Decisions ----
            //Ask player if they want to buy -> how many acres to buy
            acresToBuy = askHowManyAcresToBuy(landPrice, bushelsInStorage);
            if (acresToBuy > 0) {
                bushelsInStorage = bushelsInStorage - (acresToBuy * landPrice);
                acresOwned = acresOwned + acresToBuy;
            } else {
                // if dont buy -> ask player how many acres to sell
                acresToSell = askHowManyAcresToSell(acresOwned);
                bushelsInStorage = bushelsInStorage + (acresToSell * landPrice);
                acresOwned = acresOwned - acresToSell;
            }

            // Ask how much grain to feed people
            bushelsToFeed = askHowMuchGrainToFeedPeople(bushelsInStorage);
            bushelsInStorage = bushelsInStorage - bushelsToFeed;

            // Ask how many acres to plant
            acresToPlant = askHowManyAcresToPlant(acresOwned, population, bushelsInStorage);
            bushelsInStorage = bushelsInStorage - (acresToPlant * 2); // 2 bushels per acre to plant

    
            //------Yearly Calculations ------

            //update population after a plague
            int plagueDeathsThisYear=plagueDeaths(population);
            population = population - plagueDeathsThisYear;

            //update how many people starved
            int starvedThisYear = starvationDeaths(population, bushelsToFeed);
            totalStarvedOverTerm= totalStarvedOverTerm + starvedThisYear;
            population = population - starvedThisYear;

            //check if there is an uprising
            if(uprising(population, starvedThisYear)) {
                System.out.println("O great Hammurabi, your people are revolting! You have been overthrown!");
                gameOver = true; //end the game
                break; //exit the game loop immediately
            }

            //immigrants arrive if no one starved that year
            int immigrantsThisYear=0;
            if(starvedThisYear == 0) {
                immigrantsThisYear = immigrants(population,acresOwned,bushelsInStorage);
                //add immigrants
                population = population + immigrantsThisYear;
            }


            //calculate harvest total
            int harvestedBushels = harvest(acresToPlant, acresToPlant*2);
            bushelsInStorage = bushelsInStorage + harvestedBushels;

            //calculate grain eaten by rats
            int ratsAteThisYear = grainEatenByRats(bushelsInStorage);
            bushelsInStorage = bushelsInStorage - ratsAteThisYear;

            //get land price for next year
            landPrice = newCostOfLand();


            ///----- Update previous year variables -----
            prevPeopleStarved= starvedThisYear;
            prevNewImmigrants = immigrantsThisYear;
            prevYieldPerAcre= harvestedBushels / acresToPlant;
            prevHarvestedBushels=harvestedBushels;
            prevRatsAte= ratsAteThisYear;

            //increment year
            year++;

        }

        //-----Finals Summary------
        if(!gameOver) {
            finalSummary(totalStarvedOverTerm, acresOwned, population);
        }

        scanner.close();
    }

    //-----Get Input From The User----- ***
    int getNumber(String message) {
        while (true) {
            System.out.print(message);
            if (scanner.hasNextInt()) {
                return scanner.nextInt();
            } else {
                System.out.println("Invalid input. Please enter a number.");
                scanner.next(); // clear the invalid input
            }
        }
    }


    //-----Player Decisions Methods-----

    int askHowManyAcresToBuy(int landPrice, int bushelsInStorage) {
        // Implement the method to ask the player how many acres they want to buy
       while (true) {
           int acresToBuy = getNumber("O great Hammurabi, how many acres shall you buy? (Current price: " + landPrice + " bushels/acre, You have: " + bushelsInStorage + " bushels): ");
           if (acresToBuy < 0) {
               System.out.println("O Great Hammurabi, you cannot buy a negative amount of land!");
           } else if ((acresToBuy * landPrice) > bushelsInStorage) {
               System.out.println("O Great Hammurabi, surely you jest! We have only " + bushelsInStorage + " bushels left!");
           } else {
                return acresToBuy;
            }
        }
    }

    int askHowManyAcresToSell(int acresOwned) {
        // Implement the method to ask the player how many acres they want to sell
        while (true) {
            int acresToSell = getNumber("O great Hammurabi, how many acres shall you sell? (You own: " + acresOwned + " acres): ");
            if (acresToSell < 0) {
                System.out.println("O Great Hammurabi, you cannot sell a negative amount of land!");
            } else if (acresToSell > acresOwned) {
                System.out.println("O Great Hammurabi, you cannot sell more land than you own!");
            } else {
                return acresToSell;
            }
        }
    }

    int askHowMuchGrainToFeedPeople(int bushelsInStorage) {
        // Implement the method to ask the player how much grain they want to feed the people
        while (true) {
            int bushelsToFeed = getNumber("O great Hammurabi, how much grain shall you feed the people? (You have: " + bushelsInStorage + " bushels): ");
            if (bushelsToFeed < 0) {
                System.out.println("O Great Hammurabi, you cannot feed a negative amount of grain!");
            } else if (bushelsToFeed > bushelsInStorage) {
                System.out.println("O Great Hammurabi, surely you jest! We have only " + bushelsInStorage + " bushels left!");
            } else {
                return bushelsToFeed;
            }
        }
    }

    int askHowManyAcresToPlant(int acresOwned, int population, int bushelsInStorage) {
        // Implement the method to ask the player how many acres they want to plant
        while(true) {
            int acresToPlant = getNumber("O great Hammurabi, how many acres shall you plant? (You own: " + acresOwned + " acres, You have: " + bushelsInStorage + " bushels, Population: " + population + "): ");
            if (acresToPlant < 0) {
                System.out.println("O Great Hammurabi, you cannot plant a negative amount of land!");
            } else if (acresToPlant > acresOwned) {
                System.out.println("O Great Hammurabi, you cannot plant more land than you own!");
            } else if (acresToPlant * 2 > bushelsInStorage) { // 2 bushels per acre
                System.out.println("O Great Hammurabi, you only have " + bushelsInStorage + " bushels to plant " + acresToPlant + " acres! You need " + (acresToPlant * 2) + " bushels.");
            } else if (acresToPlant > population * 10) {
                System.out.println("O Great Hammurabi, you do not have enough people to tend to that many acres!");
            } else {
                return acresToPlant;
            }
        }
    }

    //-----Game Calculation Methods-----

    // Calculate the number of deaths due to plague
    public int plagueDeaths (int population) {
        // 15% chance of plague -- if plague occurs, 50% of the population dies
        if(rand.nextInt(100) < 15) { // 15%
            return population / 2; // 50% 
        }
        //return 0 if no plague
        return 0; 
    }

    // Calculate the number of deaths due to starvation
    public int starvationDeaths(int population, int bushelsInStorage) {
        // Each person needs 20 bushels to survive for the year
        int grainNeeded = population * 20; 
        // If there is not enough grain, calculate how many people starve
        if (bushelsInStorage < grainNeeded) {
            int starvedPeople = (grainNeeded - bushelsInStorage +19) / 20;
            return Math.min(starvedPeople, population); // return the number of people who starved, but not more than the population
        }
        //if there is enough grain --> no one starves, return 0
        return 0; 
    }

    //Calculate uprising -> if more than 45% of the population starves, there is an uprising
    public boolean uprising(int population, int howManyPeopleStarved) {
       return howManyPeopleStarved * 100 > population * 45; //if more than 45% of the population starved, return true
    }

    // Calculate the number of immigrants that will come to the city
    //use: (20 * _number of acres you have_ + _amount of grain you have in storage_) / (100 * _population_) + 1
    public int immigrants(int population, int acresOwned, int grainInStorage) {
        return (20*acresOwned + grainInStorage) / (100 * population) + 1; 
    }

    // Calculate the harvest yield per acre
    public int harvest(int acres, int bushelsUsedAsSeed) {
        // Random yield between 1 and 6 bushels per acre
        int yieldPerAcre = rand.nextInt(6) + 1; 
        // Calculate total harvest
        return acres * yieldPerAcre; 
    }

    //Calculate grain eaten by rats
    public int grainEatenByRats(int bushels) {
        //40% chance of rat infestation
        if (rand.nextInt(100) < 40) { 
            // Randomly eat between 10 and 20% of the grain
            int percentEaten = 10 + rand.nextInt(21); // 10% to 30%
            return percentEaten;
        }
        //if no infestation --> return 0
        return 0;
    }

    //calculate the new cost of land
    public int newCostOfLand() {
        //Price of land is random but ranges from 17 to 23 --> (7 numbers in between 17 and 23)
        return rand.nextInt(7) + 17;

    }

    //-----Random Number Generation Methods-----

    //generate a random number between min and max --> inclusive so +1
    public int getRandomNumber(int min, int max) {
        return rand.nextInt(max - min + 1) + min;
    }


    //-----SUMMARY METHODS FOR PRINTING OUTPUT -----

    // Print the summary of the year
    void printSummary(int year, int peopleStarvedPrev, int newImmigrantsPrev, int currentPopulation,
                      int totalHarvestedPrev, int yieldPerAcrePrev, int ratsAtePrev,
                      int currentBushelsInStorage, int currentAcresOwned, int currentLandPrice) {
        System.out.println("\nO great Hammurabi!");
        System.out.println("You are in year " + year + " of your ten year rule.");
        System.out.println("In the previous year " + peopleStarvedPrev + " people starved to death.");
        System.out.println("In the previous year " + newImmigrantsPrev + " people entered the kingdom.");
        System.out.println("The population is now " + currentPopulation + ".");
        System.out.println("We harvested " + totalHarvestedPrev + " bushels at " + yieldPerAcrePrev + " bushels per acre.");
        System.out.println("Rats destroyed " + ratsAtePrev + " bushels, leaving " + currentBushelsInStorage + " bushels in storage.");
        System.out.println("The city owns " + currentAcresOwned + " acres of land.");
        System.out.println("Land is currently worth " + currentLandPrice + " bushels per acre.");
    }

    //Print final summary at the end of the game
    void finalSummary(int totalStarvedOverTerm, int finalAcresOwned, int finalPopulation) {
        System.out.println("\n---Game Over!---");
         System.out.println("After 10 years, your rule has ended.");
        System.out.println("Total people starved over the 10 years: " + totalStarvedOverTerm);
        System.out.println("Final population: " + finalPopulation);
        System.out.println("Final acres owned: " + finalAcresOwned);
    }
}