//
//  ValidateHelper.swift
//  Naber
//
//  Created by 王淳彦 on 2018/7/28.
//  Copyright © 2018年 Melone.L.T.D. All rights reserved.
//

//import Foundation
import UIKit

class ValidateHelper {
    
    static let shared = ValidateHelper()
    
    /// // 十六進位色碼轉 RGB
    /// - Parameter hex: 十六進位色碼
    /// - Returns: UIColor
    func hexStringToUIColor(hex: String) -> UIColor {
        var cString: String = hex.trimmingCharacters(in: .whitespacesAndNewlines).uppercased()
        
        if (cString.hasPrefix("#")) {
            cString.remove(at: cString.startIndex)
        }
        
        // 預設顏色
        if (cString.count != 6) {
            return UIColor.gray
        }
        
        var rgbValue:UInt32 = 0
        Scanner(string: cString).scanHexInt32(&rgbValue)
        
        return UIColor(
            red: CGFloat((rgbValue & 0xFF0000) >> 16) / 255.0,
            green: CGFloat((rgbValue & 0x00FF00) >> 8) / 255.0,
            blue: CGFloat(rgbValue & 0x0000FF) / 255.0,
            alpha: CGFloat(1.0)
        )
    }
    
    /// 是否為合法的email
    /// - Parameter email: _
    /// - Returns: _
    func isValidEmail(withEmail email: String) -> Bool {
        let emailRegularExpression = "[A-Z0-9a-z._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,64}"
        let emailNSPredicate = NSPredicate(format:"SELF MATCHES %@", emailRegularExpression)
        return emailNSPredicate.evaluate(with: email)
    }
    
    /// 是否為合法的行動電話
    /// - Parameter cellPhone: _
    /// - Returns: _
    func isVaildTelPhone(withCellPhone cellPhone: String) -> Bool {
        let cellPhoneRegularExpression = "^[0-9]{10}$"
        let cellPhoneNSPredicate = NSPredicate(format:"SELF MATCHES %@", cellPhoneRegularExpression)
        return cellPhoneNSPredicate.evaluate(with: cellPhone)
    }
    
    /// 是否為數字
    /// - Parameter number: _
    /// - Returns: _
    func isNUmber(withNumber number: String) -> Bool {
        return !number.isEmpty && number.rangeOfCharacter(from: CharacterSet.decimalDigits.inverted) == nil
    }
//    public static boolean phoneNumber(String number) {
//    if (Strings.isNullOrEmpty(number)) {
//    return false;
//    }
//    return  number.matches("(09)+[\\d]{8}");
//    }
//    static func isCellPhone(withCellPhone phone: String ) -> Bool {
//        let cellPhoneRegularExpression = ("(09)+[\\d]{8}")
//        let cellPhoneNSPredicate = NSPredicate(format:"SELF MATCHES %@", cellPhoneRegularExpression)
//        return cellPhoneNSPredicate.evaluate(with: phone)
//    }
    /// 密碼是否為6~20長度＆英數組合,不可特殊符號
    /// - Parameter password: _
    /// - Returns: _
    static func validatePass(withPassword password: String) -> Bool {
        let passExpression = "^(?=.*[a-zA-Z]+)(?=.*\\d+)[a-zA-Z0-9]{6,20}$"
        let passPredicate = NSPredicate(format:"SELF MATCHES %@", passExpression)
        return passPredicate.evaluate(with: password)
    }
    
    /// 依傳入的日期格式驗證是否合法的日期格式
    /// - Parameter strFormat: 日期格式
    /// - Returns: _
    func isValidDate(withFormat strFormat: String, strDate date: String) -> Bool {
        let dateFormatter = DateFormatter()
        dateFormatter.dateFormat = strFormat
        if let _ = dateFormatter.date(from: date) {
            // vaild format
            return true
        } else {
            // invalid format
            return false
        }
    }
    
}
