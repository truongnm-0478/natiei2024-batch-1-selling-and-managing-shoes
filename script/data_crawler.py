import requests
import random
import json
from bs4 import BeautifulSoup

url = "https://ananas.vn/wp-admin/admin-ajax.php?action=filterFollow&gender=men%2Cwomen&category=shoes&attribute=&relation=and&paged"

current_page = 1
response = requests.get(f'{url}={current_page}')
json_data = response.json()

# Get all constants
constant_rows = []
for value in ["35", "36", "37", "38", "39", "40", "41", "42", "43", "44"]:
    type = "Size"
    constant_rows.append({"id": len(constant_rows) + 1, "type": type, "value": value})
for idx in [["2", "Style"], ["3", "Category"], ["9223372036854775802", "Material"]]:
    type = idx[1]
    child = json_data["Attribute"][idx[0]]["child"]
    for child_idx in child:
        value = child[child_idx]["name"].split("|")[0]
        constant_rows.append({"id": len(constant_rows) + 1, "type": type, "value": value})

def get_constant_id(value: str, is_contains: bool = False, debug: bool = False) -> str:
    if debug:
        print(f"{value}")
    if not is_contains:
        for constant in constant_rows:
            if constant["value"].replace("-", " ").lower() == value.lower():
                return constant["id"]
    else:
        for constant in constant_rows:
            if value.lower() in constant["value"].replace("-", " ").lower():
                return constant["id"]
    return None

# Get all products
product_rows = []
product_detail_rows = []
product_image_rows = []
while(len(json_data["listProduct"])>0):
    print("Current page: ", current_page)
    for product in json_data["listProduct"]:
        try:
            # product detail page
            soup = BeautifulSoup((requests.get(product["linkBuyNow"])).content, "html.parser")

            name = product["productName"].split(" - ")[0].replace("'", "")
            quantity = product["statusQuantity"]
            price = int(product["productPrice"].split(" ")[0].replace(".", ""))
            origin_price = price - random.randrange(100000, 200000, 10000)
            images = [img["src"] for img in soup.find("div", class_="prd-detail-slide1").find_all("img")]
            gender = 2 if random.random() > 0.4 else random.randrange(0, 2, 1)
            gender_val = None
            if gender == 2:
                gender_val = "UNISEX"
            elif gender == 1:
                gender_val = "MALE"
            else:
                gender_val = "FEMALE"

            description = {"images": [soup.find("div", class_="col-xs-12 col-sm-12 col-md-5 col-lg-5 prd-detail-right").find_all("img")[-1]["src"]]}

            # style id
            style_name = product["productName"].split(" - ")[1]
            style_id = get_constant_id(style_name)

            # category id
            category_name = soup.find("ol", class_="breadcrumb").find_all("a")[1].text.strip()
            category_id = get_constant_id(category_name)

            # material id
            material_name = None
            span = soup.find('span', attrs={'data-sheets-value': True})
            if span:
                text = span.get_text(separator='').strip()
                material_name = text.split('\n')[2].split(': ')[1]
            else:
                divs = soup.find_all('div', class_='TypographyPresentation TypographyPresentation--m RichText3-paragraph--withVSpacingNormal RichText3-paragraph')
                for div in divs:
                    if div.get_text(separator=' ').strip().startswith('Upper'):
                        material_name = div.get_text(separator=' ').strip().split(': ')[1]
                        break
            material_id = get_constant_id(material_name.split("/")[0].split(" ")[0], is_contains=True)

            # color ids
            colors = [span["style"].split("#")[-1][:-1] for span in soup.find('li', class_="cb-color-fixed").find_all("span", class_="bg-color")]
            color_ids = []
            for color in colors:
                color_id = get_constant_id(color, debug=False)
                if not color_id:
                    color_id = len(constant_rows) + 1
                    constant_rows.append({"id": color_id, "type": "Color", "value": color})
                color_ids.append(color_id)

            if style_id is not None and category_id is not None and material_id is not None and color_ids:
                product_id = len(product_rows) + 1
                product_rows.append({"id": product_id, "origin_price": origin_price, "name": name, "gender": gender_val, "description": json.dumps(description), "category_id": category_id, "style_id": style_id, "material_id": material_id})
                # image rows
                for image in images:
                    product_image_rows.append({"id": len(product_image_rows) + 1, "product_id": product_id, "url": image})
                # product detail rows
                size_ids = [row["value"] for row in constant_rows if row["type"] == "Size"]
                for size_id in size_ids:
                    for color_id in color_ids:
                        product_detail_rows.append({"id": len(product_detail_rows) + 1, "quantity": quantity, "price": price, "product_id": product_id, "color_id": color_id, "size_id": size_id})

        except Exception as e:
            print("Error: ", name, e)
            continue

    current_page += 1
    response = requests.get(f'{url}={current_page}')
    json_data = response.json()


if __name__ == "__main__":
    with open("./database/initial_data.sql", "a") as f:
        print("Seeding constant data...")
        f.write("\n\n-- Constant data\n")
        for constant in constant_rows:
            f.write(f"INSERT INTO constants (id, type, value) VALUES ({constant['id']}, '{constant['type']}', '{constant['value']}');\n")
        print("Seeding product data...")
        f.write("\n\n-- Product data\n")
        for product in product_rows:
            f.write(f"INSERT INTO products (id, origin_price, name, gender, description, category_id, style_id, material_id) VALUES ({product['id']}, {product['origin_price']}, '{product['name']}', '{product['gender']}', '{product['description']}', {product['category_id']}, {product['style_id']}, {product['material_id']});\n")
        print("Seeding product_image data...")
        f.write("\n\n-- Product image data\n")
        for product_image in product_image_rows:
            f.write(f"INSERT INTO product_images (id, product_id, url) VALUES ({product_image['id']}, {product_image['product_id']}, '{product_image['url']}');\n")
        print("Seeding product_detail data...")
        f.write("\n\n-- Product detail data\n")
        for product_detail in product_detail_rows:
            f.write(f"INSERT INTO product_details (id, quantity, price, product_id, color_id, size_id) VALUES ({product_detail['id']}, {product_detail['quantity']}, {product_detail['price']}, {product_detail['product_id']}, {product_detail['color_id']}, {product_detail['size_id']});\n")

