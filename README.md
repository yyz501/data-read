现在的问题是 应该没有成功load路径中的文件，因为在测试 case1 的时候，customer ID检测到，但是和load文件做数据对比出错了。
测试路径举例：
case 1:$java TicketManagementEngine --customer 1 abc@123 ../assets/customer.csv ../assets/concert.csv ../assets/bookings.csv ../assets/venue_mcg.txt ../assets/venue_marvel.txt
结果：比对结果成功 print welcome message
case 2: $java TicketManagementEngine --customer ../assets/customer.csv ../assets/concert.csv ../assets/bookings.csv ../assets/venue_mcg.txt 
结果 ：Enter your name: Jane Doe
      Enter your password: abc#1234
case 3: $java TicketManagementEngine --customer 1 abc@1232 ../assets/customer.csv ../assets/concert.csv ../assets/bookings.csv ../assets/venue_mcg.txt ../assets/venue_marvel.txt
结果： Incorrect Password. Terminating Program
case 4: $java TicketManagementEngine --customer 100 abc@1232 ../assets/customer.csv ../assets/concert.csv ../assets/bookings.csv ../assets/venue_mcg.txt ../assets/venue_marvel.txt
结果： Customer does not exist. Terminating Program
