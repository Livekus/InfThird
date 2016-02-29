package com.wittakarn.inflow.view;



import java.io.*; 
import javax.swing.*;
import java.util.*;
import java.awt.*;
class Trans {
    //Variable in Class
   static BufferedReader keyboard = new BufferedReader(new InputStreamReader(System.in)); // รับค่าจาก Keyboard
   static public JTextArea outputTextArea = new JTextArea(10,50) ; //กำหนดค่า textarea
   static String mtch[] = {"","หนึ่ง","สอง","สาม","สึ่","ห้า","หก","เจ็ด","แปด","เก้า"};
   static String mtdec[]={"พัน","ร้อย","สิบ","ล้าน","แสน","หมื่น","พัน","ร้อย","สิบ",""} ;
   static String mstnum="",mstthai="",choice1="",cnumthai="",cin_number = "",tmp_key="" ;
   static double  choice=0;
   static int ncnt = 1,nlen = 0,nlen2 = 0,nnumber=0,din_number2 = 0 ;
      //-------------------------------
     public static void main(String[] args) throws IOException // main method
    {
         JOptionPane.showMessageDialog(null, "จากตัวเลขเป็นตัวอักษร");
         String tmp ;
        do {
            showmath("");
            tmp = JOptionPane.showInputDialog(null,outputTextArea,"คำตอบ",JOptionPane.QUESTION_MESSAGE);
            tmp_key = tmp ;
            if (tmp== null ) {
                break;
            } else {
                choice = Double.parseDouble(tmp);
               spell(choice);
            }
        } while (choice != -1);
   }

    //spell
    public static String spell(Double in_number){
   if ((in_number > 9999999999.99) || (in_number <= 0)) {
        return "" ;
   }
    cnumthai = "" ;
    din_number2 = (int) (in_number*100) ;
    cin_number = Integer.toString(din_number2) ;
    mstnum = cin_number ;
    nlen = cin_number.length() ;
    nlen2= 12-nlen ;
    //add "x"
    for (int xj=0;xj<nlen2;xj++){
        mstnum = "x"+mstnum ;
    }
    for(int xe =0;xe<10;xe++){
        String  ccharnum = mstnum.substring(xe,xe+1) ;
        if (ccharnum.equals("x")){
            cnumthai = cnumthai + "" ;
       }else{
           if (ccharnum.equals("0")){
               if (xe==4){
                    cnumthai= cnumthai + "ล้าน" ;
               }else{
                    cnumthai = cnumthai + "" ;
               }
            }else{
                   if (ccharnum.equals("1")){
                   if ((xe==3&&nlen!=9)||(xe==9&&nlen!=3)){
                        cnumthai = cnumthai+"เอ็ด";
                   }else {
                       if (xe == 8) {
                            System.out.println("-----------------------------");
                        }else{
                        if (xe!=2||xe!=8) {
                            cnumthai=cnumthai + mtch[Integer.parseInt(ccharnum)];
                        }
                       }
                        
                   }
               }else{
                   if (ccharnum.equals("2")&&(xe==2||xe==8)) {
                            cnumthai=cnumthai + "ยี่";
                    }else{
                            cnumthai=cnumthai + mtch[Integer.parseInt(ccharnum)];
                    }
               }
               cnumthai = cnumthai+ mtdec[xe] ;
           }
       }
    }
    cnumthai = cnumthai+"บาท" ;
    //decimal
    String ccharnum = mstnum.substring(10) ;
    if (ccharnum.equals("00")){
        cnumthai = cnumthai+"ถ้วน" ;
   } else{
        ccharnum = mstnum.substring(10,11) ;
        if (!ccharnum.equals("0")){
            if (!ccharnum.equals("1")){
               if (ccharnum.equals("2")){
                  cnumthai = cnumthai+"ยี่" ;
               }else{
                   cnumthai = cnumthai+mtch[Integer.parseInt(ccharnum)] ;
               }
            }
            cnumthai = cnumthai+mtdec[8] ;
        }
         String ccharnum1 = mstnum.substring(11) ;
            if (!ccharnum1.equals("0")){
                if (ccharnum1.equals("1")||ccharnum.equals("0")){
                      cnumthai = cnumthai+"เอ็ด" ;
                   }else{
                       cnumthai = cnumthai+mtch[Integer.parseInt(ccharnum1)] ;
                   }
            }
        cnumthai = cnumthai+"สตางค์" ;
       }
        return cnumthai ;
    }
    public static String  showmath(String mnext)
    {
        outputTextArea.setText(null);  //ล้างข้อมูล
        outputTextArea.append("\n")  ;
        outputTextArea.append("KEY :     "+tmp_key)  ;
        outputTextArea.append("\n")  ;
        outputTextArea.append("\n")  ;
        outputTextArea.append("แปลเป็นภาษาไทย\n")  ;
        outputTextArea.append("\n"+cnumthai)  ;
        outputTextArea.append("\n")  ;
        outputTextArea.setFont(new Font("Tohoma", Font.BOLD, 15));
        outputTextArea.setLineWrap(true);
        outputTextArea.setWrapStyleWord(true);
         return mnext ;
    }
}
 
// end code