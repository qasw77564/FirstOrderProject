//
//  ImageHelper.swift
//  Naber
//
//  Created by 王淳彦 on 2018/8/8.
//  Copyright © 2018年 Melone.L.T.D. All rights reserved.
//

import Foundation
import UIKit
import Firebase

class ImageHelper {

    static func resizeImage( originalImage: UIImage, minLenDP: Int ) -> UIImage! {
        let dp: CGFloat = CGFloat(minLenDP * 4)
        let width: CGFloat =  originalImage.size.width
        let height: CGFloat = originalImage.size.height
        
        if width <= dp && height <= dp {
            return originalImage
        }
        
        let maxLen: CGFloat = max(width, height)
        let ratio: CGFloat = maxLen / dp
        let changedheight: CGFloat =  height / ratio
        let changedWidth: CGFloat =  width / ratio
        
        UIGraphicsBeginImageContext(CGSize(width: changedWidth, height: changedheight))
        originalImage.draw(in: CGRect(x: 0, y: 0, width: changedWidth, height: changedheight))
        let resizedImage = UIGraphicsGetImageFromCurrentImageContext()
        UIGraphicsEndImageContext()

        return resizedImage
    }
    
    static func getReference(path: String, child: String, fileName: String ) -> StorageReference! {
        
        if Model.CURRENT_FIRUSER == Optional.none {
            return Optional.none
        } else {
            return Storage.storage(url: path).reference().child(child + fileName)
        }
    }
    
    // sourcePath == "/user/" || "/restaurant/food/"
    static func upLoadImage(data: Data, sourcePath: String, fileName: String, onGetUrl: @escaping (URL) -> (), onFail: @escaping (String) -> ()){
        Loading.show()
        let metadata = StorageMetadata()
        metadata.contentType = "image/jpeg"

        if let ref: StorageReference = getReference(path: NaberConstant.STORAGE_PATH, child:sourcePath, fileName: fileName) {
            ref.putData(data, metadata: metadata) { metadata, error in
                ref.downloadURL { (url, error) in
                    guard let downloadURL = url else {
                        onFail("upload failure")
                        Loading.hide()
                        return
                    }
                    
                    if error != nil {
                        onFail("upload failure")
                        Loading.hide()
                    }else {
                        onGetUrl(downloadURL)
                        Loading.hide()
                    }
                }
                }.observe(.failure) { snapshot in
                    onFail("upload failure")
                    Loading.hide()
            }
        }else {
            onFail("CURRENT_FIRUSER not atuh failure")
            Loading.hide()
        }
      
        
    }

}
