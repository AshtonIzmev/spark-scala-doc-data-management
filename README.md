# spark-scala-doc-data-management

A scala documentation for data management as code

The scala doc precedes every dataframe "data product" in the code as the following :

```
/**
* @description My little Data Product
* @owner [[owner_issam]]
* @custodian [[custodian_sami]]
* @database [[database_khalid_db]]
* @table [[table_anas_tb]]
* @key column2
* @return [[dictionnaryStr_dataproduct_mainRun]]
* @quality unique(column1);unique(column2);date_shape(column3,dd-MM-yyyy)
*/
def getDataProduct: DataFrame =
    Seq(
      ("value1", "NA", "21-12-2021"),
      ("value2", "NA", "18-12-2025")
    ).toDF("column1", "column2", "column3")
```

# Why ?
Instead of using external data management tools, data teams could build their own data management stack directly in the code inside comments.

# What ?
The main idea is here to 
* check data quality using deequee following the rules in the comments
* provide an easy way to track metadata info for data products
* output an easy-to-read version of all this information in an external tool (static page for example)

# When ?
This is especially usefull for small tech teams that do not have business data owners (who would need dedicated tools and UI).

# Maintainers
* https://github.com/anasmeskine
* https://github.com/khalid-sudo
* https://github.com/SAMI-li
* https://github.com/AshtonIzmev