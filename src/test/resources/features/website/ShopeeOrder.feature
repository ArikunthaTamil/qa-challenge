Feature: Order Product from search results
  @Web
  Scenario Outline: Select and Add highest price product within five pages from search results
    Given I'm on shopee home page
    And User already logged in and cart is empty
    When I enter search "<Keyword>" and click on search button
    Then I should see the search results header and lists
    And I navigate through first "<Pagination>" pages and store all item price
    And I Calculate maximum item price and click on the item
    And I enter "<Qty>" no of items to buy
    And I store all the details of item
    #When I click on add to card
    #Then I should see the cart updated with same "<Qty>" no of items
    Examples:
      |Keyword |Qty |Pagination|
      |toy     |8   |5         |