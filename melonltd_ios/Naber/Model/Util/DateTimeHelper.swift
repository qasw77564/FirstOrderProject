//
//  DateTimeHelper.swift
//  Naber
//
//  Created by 王淳彦 on 2018/7/24.
//  Copyright © 2018年 Melone.L.T.D. All rights reserved.
//

import Foundation


class DateTimeHelper {  
    static var formatter: DateFormatter = getFormatter()

    static func getFormatter() -> DateFormatter {
        let form: DateFormatter = DateFormatter()
        form.timeZone = TimeZone.init(identifier: "Asia/Taipei")
        form.locale = Locale.init(identifier: "zh_TW")
        form.dateFormat = "yyyy-MM-dd'T'HH:mm:ss.SSSS'Z'"
        return form
    }
    
    static func getNowMilliseconds() -> Int {
        return Int(Date().timeIntervalSince1970 * 1000)
    }
    
    static func getMiliseconds(date: Date) -> Int {
        return Int(date.timeIntervalSince1970 * 1000)
    }
    
    static func getNow() -> String  {
        formatter.dateFormat = "yyyy-MM-dd'T'HH:mm:ss.SSSS'Z'"
        return  formatter.string(from: Date())
    }
    
    static func getNow(from: String) -> String  {
        formatter.dateFormat = from
        return  formatter.string(from: Date())
    }
    
    static func stringToDate(date: String) -> Date {
        formatter.dateFormat = "yyyy-MM-dd'T'HH:mm:ss.SSSS'Z'"
        let date = formatter.date(from: date)
        return date!
    }
    
    static func stringToDate(date: String, from: String ) -> Date {
        formatter.dateFormat = from
        let date = formatter.date(from: date)
        return date!
    }
    
    static func dateToString(date: Date) -> String {
        formatter.dateFormat = "yyyy-MM-dd'T'HH:mm:ss.SSSS'Z'"
        let date = formatter.string(from: date)
        return date
    }
    
    static func dateToString(date: Date, form: String) -> String {
        formatter.dateFormat = form
        let date = formatter.string(from: date)
        return date
    }
    
    static func dateToStringForm(date: Date, form: String) -> String {
        formatter.dateFormat = form
        let date = formatter.string(from: date)
        return date
    }
    
    static func formToString(date: String, from: String) -> String {
        let d: Date = stringToDate(date: date)
        return dateToStringForm(date: d, form: from)
    }
    
    static func formToString(date: String, fromDate: String) -> String {
        let d: Date = stringToDate(date: date, from: fromDate)
        return dateToString(date: d)
    }
    
    // 取得當下 00:00:00 to date
    static func startOfDate() -> Date {
        return Locale.init(identifier: "zh_TW").calendar.startOfDay(for: Date())
    }
    
    // 取得當下 00:00:00 to string
    static func startOfDateString() -> String {
        return dateToString(date: startOfDate())
    }
    
    // 取得傳入時間的 00:00:00  to string
    static func forDateStart(date: Date) -> String {
        return dateToString(date: Locale.init(identifier: "zh_TW").calendar.startOfDay(for: date))
    }
    
    //取得當下 00:00:00 to Miliseconds
    static func startOfDateMiliseconds() -> Int {
        return getMiliseconds(date: Locale.init(identifier: "zh_TW").calendar.startOfDay(for: startOfDate()))
    }
    
    //取得當下 00:00:00 to Miliseconds
    static func startOfDateMiliseconds(date: Date) -> Int {
        return getMiliseconds(date: Locale.init(identifier: "zh_TW").calendar.startOfDay(for: date))
    }
    
    // 取得當下 23:59:59 to date
    static func endOfDate() -> Date {
        let components: DateComponents = DateComponents(day: 1)
        let date: Date = Calendar.current.date(byAdding: components, to: startOfDate())!
        return date.addingTimeInterval(-1)
    }
    
    // 取得當下 23:59:59 to string
    static func endOfDateString() -> String {
        return dateToString(date: endOfDate())
    }
    
    //取得當下 23:59:59 to Miliseconds
    static func endOfDateMiliseconds() -> Int {
        let components: DateComponents = DateComponents(day: 1)
        let date: Date = Calendar.current.date(byAdding: components, to: startOfDate())!
        return getMiliseconds(date: date.addingTimeInterval(-1))
    }
    
    //取得當下 23:59:59 to Miliseconds
    static func endOfDateMiliseconds(date: Date) -> Int {
        let components: DateComponents = DateComponents(day: 1)
        let date: Date = Calendar.current.date(byAdding: components, to: date)!
        return getMiliseconds(date: date.addingTimeInterval(-1))
    }
    
    
    // 取得傳入時間的 23:59:59 to string
    static func forDateDnd(date: Date) -> String {
        let components: DateComponents = DateComponents(day: 1)
        let date: Date = Calendar.current.date(byAdding: components, to: Locale.init(identifier: "zh_TW").calendar.startOfDay(for: date))!
        return dateToString(date: date.addingTimeInterval(-1))
    }
    
    // get puls ?Day 00:00:00 to string
    static func startOfDate(day plus: Int) -> String {
        let components: DateComponents = DateComponents(day: plus)
        let date: Date = Calendar.current.date(byAdding: components, to: startOfDate())!
        return dateToString(date: date)
    }
    
    // get puls ?Day 23:59:59 to string
    static func endOfDate(day plus: Int) -> String {
        let components: DateComponents = DateComponents(day: plus + 1)
        let date: Date = Calendar.current.date(byAdding: components, to: startOfDate())!
        return dateToString(date: date.addingTimeInterval(-1))
    }
    
    // 依照傳入日期取得週
    static func getWeekDate(date: String) -> String {
        return NaberConstant.WEEK_DAY_NAME[Calendar.current.component(.weekday, from: stringToDate(date: date)) - 1]
    }
}
