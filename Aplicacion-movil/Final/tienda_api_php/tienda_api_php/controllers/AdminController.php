<?php
require_once __DIR__ . "/../lib/http.php";
require_once __DIR__ . "/../models/Productos.php";
require_once __DIR__ . "/../models/Ventas.php";

class AdminController {
  public static function createProducto(): void {
    $data = read_json();
    require_fields($data, ["marca","tipo","precio","stock"]);
    $id = Productos::create($data);
    json_response(["ok"=>true, "data"=>["id"=>$id]], 201);
  }

  public static function updateProducto(int $id): void {
    $data = read_json();
    require_fields($data, ["marca","tipo","precio","stock"]);
    if (!Productos::find($id)) json_response(["ok"=>false, "error"=>"Producto no encontrado"], 404);
    Productos::update($id, $data);
    json_response(["ok"=>true, "data"=>["updated"=>true]]);
  }

  public static function deleteProducto(int $id): void {
    if (!Productos::find($id)) json_response(["ok"=>false, "error"=>"Producto no encontrado"], 404);
    Productos::softDelete($id);
    json_response(["ok"=>true, "data"=>["deleted"=>true]]);
  }

  public static function ventas(): void {
    $list = Ventas::listAdmin(500);
    json_response(["ok"=>true, "data"=>["ventas"=>$list]]);
  }

  public static function ventasDetalle(int $id): void {
    $det = Ventas::detalle($id);
    json_response(["ok"=>true, "data"=>["detalle"=>$det]]);
  }
}
