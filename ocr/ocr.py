# Import from Python File

from img_io import *
from img_process import *
import numpy as np
import os
import cv2

def print_checkpoint(text):
  print(text, " ==  DONE")

def main():
    
    # Get Images and Border Pattern
    dictionaries = [cv2.aruco.DICT_APRILTAG_16h5, cv2.aruco.DICT_APRILTAG_25h9, cv2.aruco.DICT_APRILTAG_36h10,cv2.aruco.DICT_APRILTAG_36h11]
    script_dir = os.path.dirname(os.path.abspath(__file__))

    # Membangun jalur ke file border.png yang berada di direktori yang sama dengan skrip ini
    border_path = os.path.join(script_dir, 'border.png')
    image_path = os.path.join(script_dir, "forms.jpg")
    border = read_image(border_path)
    print("INI BORDER : ", border)
    # Scale down border since initial picture is too huge
    border = resize_image(border, 0.2)
    print("Ini border di sini : ", border)
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
    gray_img = cv2.cvtColor(cropped_img, cv2.COLOR_BGR2GRAY)
    for parameter in dictionaries:
      aruco_dict = cv2.aruco.Dictionary_get(parameter)
      parameters = cv2.aruco.DetectorParameters_create()
      corners, ids, rejectedImgPoints = cv2.aruco.detectMarkers(gray_img, aruco_dict, parameters=parameters)
      print("Ini corners : ", corners)
      if len(corners) > 0:
        # Tandai marker di gambar
        cv2.aruco.drawDetectedMarkers(cropped_img, corners, ids)

        # Gambar kotak di sekitar setiap marker yang terdeteksi
        for corner in corners:
            pts = corner[0].astype(np.int32)
            cv2.polylines(cropped_img, [pts], True, (0, 0, 255), 5)
    output_path = os.path.join(script_dir, "processed_image.jpg")
    print("Ini output path", output_path)
    cv2.imwrite(output_path, cropped_img)
    return output_path
#     display_image(cropped_img, 0.5)

main()