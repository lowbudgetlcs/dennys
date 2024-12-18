let
  nixpkgs = fetchTarball "https://github.com/NixOS/nixpkgs/tarball/nixos-24.11";
  pkgs = import nixpkgs { config = {}; overlays = []; };
in pkgs.mkShellNoCC {
  packages = with pkgs; [
    nodejs_22
  ];
  shellHook = ''
    alias run="npm run dev"
  '';
}

