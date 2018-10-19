
import Foundation

class BulletinResp : Codable {
    var status : String!
    var err_code : String!
    var err_msg : String!
    var data : [BulletinVo]! = []
    
    public static func toJson(structs : BulletinResp) -> String {
        do {
            return String(data: try JSONEncoder().encode(structs), encoding: .utf8)!
        } catch {
            return ""
        }
    }
    
    public static func parse(src : String) -> BulletinResp? {
        do {
            return try JSONDecoder().decode(BulletinResp.self, from: src.data(using:.utf8)!)
        }catch {
            return nil
        }
    }
}


class BulletinVo : Codable {
    var title : String!
    var content_text : String!
    var bulletin_category : String!
    
    public static func toJson(structs : BulletinVo) -> String {
        do {
            return String(data: try JSONEncoder().encode(structs), encoding: .utf8)!
        } catch {
            return ""
        }
    }
    
    public static func parse(src : String) -> BulletinVo? {
        do {
            return try JSONDecoder().decode(BulletinVo.self, from: src.data(using:.utf8)!)
        }catch {
            return nil
        }
    }
    
}
