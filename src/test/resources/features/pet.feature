Feature: Test Pet API

Scenario: Setup a mock service for Pet for POST
    Given create a pet with given input
	| url					| /pets	             |
	| input                 |  Fish              |
    | output                |  Gold-Fish         |
    | httpStatusCode	    |   201              |
    | method                |   POST             |
    Then Check the Json is Valid
    | key | {"output":"Gold-Fish","input":"Fish","method":"POST","url":"/pets","httpStatusCode":"201"}|

  Given create a pet with given input
    | id					    |  0001	                        |
    | type                      |  donut                        |
    | name                      |  Cake                         |
    | ppu	                    |   d~0.55                      |
    | batters.batter[0].id      |   1001                        |
    | batters.batter[0].type    |   Regular                     |
    | batters.batter[1].id      |   1002                        |
    | batters.batter[1].type    |   Chocolate                   |
    | batters.batter[2].id      |   1003                        |
    | batters.batter[2].type    |   Blueberry                   |
    | batters.batter[3].id      |   1004                        |
    | batters.batter[3].type    |   Devil's Food                |
    | topping[0].id             |   5001                        |
    | topping[0].type           |   None                        |
    | topping[1].id             |   5002                        |
    | topping[1].type           |   Glazed                      |
    | topping[2].id             |   5005                        |
    | topping[2].type           |   Sugar                       |
    | topping[3].id             |   5007                        |
    | topping[3].type           |   Powdered Sugar              |
    | topping[4].id             |   5006                        |
    | topping[4].type           |   Chocolate with Sprinkles    |
    | topping[5].id             |   5003                        |
    | topping[5].type           |   Chocolate                   |
    | topping[6].id             |   5004                        |
    | topping[6].type           |   Maple                       |
  Then Check the Json is Valid
    | key |{"ppu":0.55,"batters":{"batter":[{"id":"1001","type":"Regular"},{"id":"1002","type":"Chocolate"},{"id":"1003","type":"Blueberry"},{"id":"1004","type":"Devil's Food"}]},"name":"Cake","id":"0001","type":"donut","topping":[{"id":"5001","type":"None"},{"id":"5002","type":"Glazed"},{"id":"5005","type":"Sugar"},{"id":"5007","type":"Powdered Sugar"},{"id":"5006","type":"Chocolate with Sprinkles"},{"id":"5003","type":"Chocolate"},{"id":"5004","type":"Maple"}]}|
