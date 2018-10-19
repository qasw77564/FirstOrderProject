//
//  ImageTransformer.swift
//  Naber
//
//  Created by 王淳彦 on 2018/7/21.
//  Copyright © 2018年 Melone.L.T.D. All rights reserved.
//

import Foundation
import MapleBacon



class TransformerHelper {

    public static func transformer (identifier : String) -> ImageTransformer {
        return SepiaImageTransformer(identifier: identifier).appending(transformer: VignetteImageTransformer(identifier: identifier))
    }

    private class SepiaImageTransformer: ImageTransformer {
        var identifier = ""
        init(identifier: String) {
            self.identifier = identifier
        }
        
        func transform(image: UIImage) -> UIImage? {
            guard let filter = CIFilter(name: "CISepiaTone") else { return image }
            let ciImage = CIImage(image: image)
            filter.setValue(ciImage, forKey: kCIInputImageKey)
            filter.setValue(0, forKey: kCIInputIntensityKey)
            let context = CIContext()
            guard let outputImage = filter.outputImage,
                let cgImage = context.createCGImage(outputImage, from: outputImage.extent) else { return image }
            
            return UIImage(cgImage: cgImage)
        }
    }

    
    private class VignetteImageTransformer: ImageTransformer {
        var identifier = ""
        init(identifier: String) {
            self.identifier = identifier
        }
        func transform(image: UIImage) -> UIImage? {
            guard let filter = CIFilter(name: "CIVignette") else { return image }
            let ciImage = CIImage(image: image)
            filter.setValue(ciImage, forKey: kCIInputImageKey)
            let context = CIContext()
            guard let outputImage = filter.outputImage,
                let cgImage = context.createCGImage(outputImage, from: outputImage.extent) else { return image }
            return UIImage(cgImage: cgImage)
        }
    }
}

