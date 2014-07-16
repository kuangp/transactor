trans:
	java -cp ~/Documents/transactor/salsa1.1.5.jar:. salsac.SalsaCompiler ./transactor/language/*.salsa
	javac -classpath ~/Documents/transactor/salsa1.1.5.jar:. ./transactor/language/*.java
	javac -classpath ~/Documents/transactor/salsa1.1.5.jar:. ./transactor/resources/*.java

example-bank:
	java -cp ~/Documents/transactor/salsa1.1.5.jar:. salsac.SalsaCompiler ./transactor/examples/bank_transfer/*.salsa
	javac -classpath ~/Documents/transactor/salsa1.1.5.jar:. ./transactor/examples/bank_transfer/*.java

example-bank2:
	java -cp ~/Documents/transactor/salsa1.1.5.jar:. salsac.SalsaCompiler ./transactor/examples/bank_transfer_2/*.salsa
	javac -classpath ~/Documents/transactor/salsa1.1.5.jar:. ./transactor/examples/bank_transfer_2/*.java

example-cell:
	java -cp ~/Documents/transactor/salsa1.1.5.jar:. salsac.SalsaCompiler ./transactor/examples/reference_cell/*.salsa
	javac -classpath ~/Documents/transactor/salsa1.1.5.jar:. ./transactor/examples/reference_cell/*.java

example-house:
	java -cp ~/Documents/transactor/salsa1.1.5.jar:. salsac.SalsaCompiler ./transactor/examples/house_purchase/*.salsa
	javac -classpath ~/Documents/transactor/salsa1.1.5.jar:. ./transactor/examples/house_purchase/*.java

example-all:
	@echo "\ncompiling bank example......\n"
	java -cp ~/Documents/transactor/salsa1.1.5.jar:. salsac.SalsaCompiler ./transactor/examples/bank_transfer/*.salsa
	javac -classpath ~/Documents/transactor/salsa1.1.5.jar:. ./transactor/examples/bank_transfer/*.java
	@echo "\ncompiling bank_v2 example......\n"
	java -cp ~/Documents/transactor/salsa1.1.5.jar:. salsac.SalsaCompiler ./transactor/examples/bank_transfer_2/*.salsa
	javac -classpath ~/Documents/transactor/salsa1.1.5.jar:. ./transactor/examples/bank_transfer_2/*.java
	@echo "\ncompiling cell example......\n"
	java -cp ~/Documents/transactor/salsa1.1.5.jar:. salsac.SalsaCompiler ./transactor/examples/reference_cell/*.salsa
	javac -classpath ~/Documents/transactor/salsa1.1.5.jar:. ./transactor/examples/reference_cell/*.java
	@echo "\ncompiling house example......\n"
	java -cp ~/Documents/transactor/salsa1.1.5.jar:. salsac.SalsaCompiler ./transactor/examples/house_purchase/*.salsa
	javac -classpath ~/Documents/transactor/salsa1.1.5.jar:. ./transactor/examples/house_purchase/*.java

transfer:
	java -cp ~/Documents/transactor/salsa1.1.5.jar:. transactor/examples/bank_transfer/transfer

transfer2:
	java -cp ~/Documents/transactor/salsa1.1.5.jar:. transactor/examples/bank_transfer_2/transfer

cell:
	java -cp ~/Documents/transactor/salsa1.1.5.jar:. transactor/examples/reference_cell/cell_tester

house:
	java -cp ~/Documents/transactor/salsa1.1.5.jar:. transactor/examples/house_purchase/housePurchase

