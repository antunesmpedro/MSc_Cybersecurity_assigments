import java.util.Scanner;

public class Activator {
  private static Boolean isValidSerialNumber(String paramString) {
    String[] arrayOfString = paramString.split("-");
    if (arrayOfString.length != 4)
      return Boolean.valueOf(false); 
    Integer integer1 = Integer.valueOf(Integer.parseInt(arrayOfString[0], 16));
    Integer integer2 = Integer.valueOf(Integer.parseInt(arrayOfString[1], 16));
    Integer integer3 = Integer.valueOf(Integer.parseInt(arrayOfString[2], 16));
    Integer integer4 = Integer.valueOf(Integer.parseInt(arrayOfString[3], 16));

    //integer2 == 1337 (hex) == 4919 (decimal)
    //integer1 == 2A9F (hex) == 10911 (decimal) (12248-1337)
    //integer3 == 95FE (hex) == 38398 (decimal) (4919+33479)
    //integer4 == D34D (hex) == 54093 (decimal)
    if (integer2.intValue() != 4919)
      return Boolean.valueOf(false); 
    if (integer1.intValue() + 1337 != 12248)
      return Boolean.valueOf(false); 
    if (integer3.intValue() != integer2.intValue() + 33479)
      return Boolean.valueOf(false); 
    if (integer4.intValue() != Integer.parseInt("d34d", 16))
      return Boolean.valueOf(false); 
    return Boolean.valueOf(true);
  }
  
  public static void main(String[] paramArrayOfString) {
    System.out.print("Enter Serial Number: ");
    Scanner scanner = new Scanner(System.in);
    String str = scanner.nextLine();
    // str == 2A9F-1337-95FE-D34D
    if (isValidSerialNumber(str).booleanValue()) {
      System.out.println("Success! Here is your flag: TPAS{" + str + "}");
    } else {
      System.out.println("Invalid Serial Number");
    } 
  }
}
