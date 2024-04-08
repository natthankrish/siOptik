# Import from Python File

from img_io import *
from img_process import *
import numpy as np
import os
import cv2

def print_checkpoint(text):
  print(text, " ==  DONE")

def main(image_path):
    # Get Images and Border Pattern

    script_dir = os.path.dirname(os.path.abspath(__file__))

    # Membangun jalur ke file border.png yang berada di direktori yang sama dengan skrip ini
    border_path = os.path.join(script_dir, 'border.png')
    border = read_image(border_path)
    print("Ini border :", border)
    # Scale down border since initial picture is too huge
    border = resize_image(border, 0.2)
    assert border is not None, "Border file was not detected"

    # Detecting Border
    # Use first Image as example
    img = image_path
    img = read_image(img)

    detected_img, border_center_arr = detect_border(img, border)
    assert detected_img is not None, "Border pattern was failed to be detected"
    assert len(border_center_arr) == 4, "Border center is invalid"
    print_checkpoint("Detecting Image Border")

    # Crop Image
    sorted_w = sorted(border_center_arr, key= lambda x: x[0])
    sorted_h = sorted(border_center_arr, key= lambda x: x[1])
    # cropped_img = detected_img[min_h:max_h, min_w: max_w]
    cropped_img = detected_img[int(sorted_h[0][1]): int(sorted_h[3][1]), int(sorted_w[0][0]): int(sorted_w[3][0])]

    output_path = os.path.join(script_dir, "processed_image.jpg")
    cv2.imwrite(output_path, cropped_img)
    return output_path
#     display_image(cropped_img, 0.5)

if __name__ == '__main__':
    if len(sys.argv) > 1:
        image_path = sys.argv[1]
        process_image(image_path)
    else:
        print("No image path provided")