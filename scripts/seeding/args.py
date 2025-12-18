from argparse import ArgumentParser


def args(parser: ArgumentParser):
    _ = parser.add_argument(
        "-p", "--data", help="Data file directory.", default="./scripts/data"
    )
    _ = parser.add_argument(
        "-s",
        action="store_true",
        default=False,
        help="Toggles the 'https' protocol (default '%(default)s').",
    )
    _ = parser.add_argument(
        "-e",
        "--environment",
        default="",
        help="The environment to target (default: '%(default)s').",
    )
    _ = parser.add_argument(
        "-d",
        "--domain",
        default="localhost:9292",
        help="The domain to target (default: '%(default)s').",
    )
    return parser


def base_url(parser: ArgumentParser):
    args = parser.parse_args()
    environment = args.environment
    domain = args.domain
    protocol = "http"
    if args.s:
        protocol = "https"
    if environment != "":
        environment = f"{environment}."
    return f"{protocol}://{environment}{domain}/api/v1"


def data_path(parser: ArgumentParser) -> str:
    args = parser.parse_args()
    return args.data
